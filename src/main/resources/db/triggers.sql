-- =============================================
-- COMPLETE CORRECTED TRIGGERS FILE
-- =============================================

-- Function: Automatic user ban policy when 3+ comments deleted
CREATE OR REPLACE FUNCTION check_user_ban_policy()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.deleted_comments_count >= 3 AND OLD.deleted_comments_count < 3 THEN
        UPDATE "user" 
        SET is_banned = true, 
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.id;
        
        -- notification_type 'Comentario Eliminado' has ID 5
        INSERT INTO user_notification (
            user_id, 
            notification_type_id, 
            title, 
            message, 
            reference_type
        ) VALUES (
            NEW.id,
            5, -- 'Comentario Eliminado'
            'Cuenta Suspendida',
            'Tu cuenta ha sido suspendida por acumulación de comentarios eliminados. Contacta al administrador.',
            'user_ban'
        );
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_user_ban_policy
    AFTER UPDATE OF deleted_comments_count ON "user"
    FOR EACH ROW
    EXECUTE FUNCTION check_user_ban_policy();

-- Function: Update deleted comments count when comment is marked as deleted
CREATE OR REPLACE FUNCTION update_deleted_comments_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE "user" 
    SET deleted_comments_count = deleted_comments_count + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = OLD.user_id;
    
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- comment_status 'Eliminado' has ID 2
CREATE TRIGGER trigger_deleted_comments_count
    AFTER UPDATE OF comment_status_id ON article_comment
    FOR EACH ROW
    WHEN (NEW.comment_status_id = 2)
    EXECUTE FUNCTION update_deleted_comments_count();

-- Function: Update stock automatically when stock movement is created
CREATE OR REPLACE FUNCTION update_stock_on_movement()
RETURNS TRIGGER AS $$
DECLARE
    current_stock INTEGER;
    affects_increase BOOLEAN;
BEGIN
    SELECT stock_quantity INTO current_stock 
    FROM analog_article 
    WHERE id = NEW.analog_article_id;
    
    SELECT affects_stock_increase INTO affects_increase
    FROM movement_type 
    WHERE id = NEW.movement_type_id;
    
    NEW.previous_stock := current_stock;
    
    IF affects_increase THEN
        NEW.new_stock := current_stock + NEW.quantity;
    ELSE
        NEW.new_stock := current_stock - NEW.quantity;
        
        IF NEW.new_stock < 0 THEN
            RAISE EXCEPTION 'No se puede reducir el stock por debajo de 0. Stock actual: %, Cantidad solicitada: %', current_stock, NEW.quantity;
        END IF;
    END IF;
    
    UPDATE analog_article 
    SET stock_quantity = NEW.new_stock,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.analog_article_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_stock
    BEFORE INSERT ON stock_movement
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_on_movement();

-- Function: Send low stock alerts to administrators
CREATE OR REPLACE FUNCTION check_low_stock_alert()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.stock_quantity <= NEW.min_stock_level THEN
        -- notification_type 'Stock Bajo' has ID 4
        -- user_type 'Administrador' has ID 2
        INSERT INTO user_notification (
            user_id, 
            notification_type_id, 
            title, 
            message, 
            reference_type,
            reference_id
        ) 
        SELECT 
            u.id,
            4, -- 'Stock Bajo'
            'Alerta de Stock Bajo',
            CONCAT('El artículo "', NEW.title, '" tiene stock bajo. Stock actual: ', NEW.stock_quantity, ', Mínimo: ', NEW.min_stock_level),
            'article',
            NEW.id
        FROM "user" u 
        WHERE u.user_type_id = 2; -- 'Administrador'
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_low_stock_alert
    AFTER UPDATE OF stock_quantity ON analog_article
    FOR EACH ROW
    EXECUTE FUNCTION check_low_stock_alert();

-- Function: Update cart prices when article price changes
CREATE OR REPLACE FUNCTION update_cart_prices()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.price != NEW.price THEN
        UPDATE shopping_cart_item 
        SET unit_price = NEW.price,
            updated_at = CURRENT_TIMESTAMP
        WHERE analog_article_id = NEW.id;
        
        UPDATE shopping_cart 
        SET subtotal = (
            SELECT COALESCE(SUM(quantity * unit_price - discount_applied), 0)
            FROM shopping_cart_item 
            WHERE shopping_cart_id = shopping_cart.id
        ),
        updated_at = CURRENT_TIMESTAMP
        WHERE id IN (
            SELECT DISTINCT shopping_cart_id 
            FROM shopping_cart_item 
            WHERE analog_article_id = NEW.id
        );
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_cart_prices
    AFTER UPDATE OF price ON analog_article
    FOR EACH ROW
    EXECUTE FUNCTION update_cart_prices();

-- Function: Update cart totals when items are modified
CREATE OR REPLACE FUNCTION update_cart_totals()
RETURNS TRIGGER AS $$
DECLARE
    cart_id INTEGER;
BEGIN
    IF TG_OP = 'DELETE' THEN
        cart_id := OLD.shopping_cart_id;
    ELSE
        cart_id := NEW.shopping_cart_id;
    END IF;
    
    UPDATE shopping_cart 
    SET total_items = (
        SELECT COALESCE(SUM(quantity), 0)
        FROM shopping_cart_item 
        WHERE shopping_cart_id = cart_id
    ),
    subtotal = (
        SELECT COALESCE(SUM(quantity * unit_price - discount_applied), 0)
        FROM shopping_cart_item 
        WHERE shopping_cart_id = cart_id
    ),
    updated_at = CURRENT_TIMESTAMP
    WHERE id = cart_id;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_cart_totals
    AFTER INSERT OR UPDATE OR DELETE ON shopping_cart_item
    FOR EACH ROW
    EXECUTE FUNCTION update_cart_totals();

-- Function: Update article rating statistics
CREATE OR REPLACE FUNCTION update_article_rating()
RETURNS TRIGGER AS $$
DECLARE
    article_id INTEGER;
    avg_rating DECIMAL(3,2);
    total_ratings INTEGER;
BEGIN
    IF TG_OP = 'DELETE' THEN
        article_id := OLD.analog_article_id;
    ELSE
        article_id := NEW.analog_article_id;
    END IF;
    
    SELECT 
        ROUND(AVG(rating)::DECIMAL, 2),
        COUNT(*)
    INTO avg_rating, total_ratings
    FROM article_rating 
    WHERE analog_article_id = article_id;
    
    UPDATE analog_article 
    SET average_rating = COALESCE(avg_rating, 0.00),
        total_ratings = COALESCE(total_ratings, 0),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = article_id;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_article_rating
    AFTER INSERT OR UPDATE OR DELETE ON article_rating
    FOR EACH ROW
    EXECUTE FUNCTION update_article_rating();

-- Function: Update user statistics when order is delivered
CREATE OR REPLACE FUNCTION update_user_stats_on_order()
RETURNS TRIGGER AS $$
BEGIN
    -- order_status 'Entregado' has ID 4
    IF NEW.order_status_id = 4 THEN
        UPDATE "user"
        SET total_spent = total_spent + NEW.total_amount,
            total_orders = total_orders + 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.user_id;

        UPDATE analog_article 
        SET total_sold = total_sold + oi.quantity
        FROM order_item oi
        WHERE oi.order_id = NEW.id 
        AND analog_article.id = oi.analog_article_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- order_status 'Entregado' has ID 4
CREATE TRIGGER trigger_update_user_stats
    AFTER UPDATE OF order_status_id ON "order"
    FOR EACH ROW
    WHEN (NEW.order_status_id = 4)
    EXECUTE FUNCTION update_user_stats_on_order();

-- Function: Reduce stock when order moves to processing
CREATE OR REPLACE FUNCTION reduce_stock_on_order()
RETURNS TRIGGER AS $$
BEGIN
    -- order_status: 'Procesando' has ID 2, 'Pendiente' has ID 1
    IF NEW.order_status_id = 2 AND OLD.order_status_id = 1 THEN
        -- movement_type 'Salida' has ID 2
        -- movement_reference_type 'Venta' has ID 2
        INSERT INTO stock_movement (
            analog_article_id,
            movement_type_id,
            movement_reference_type_id,
            quantity,
            reference_id,
            notes,
            created_by_user_id
        )
        SELECT 
            oi.analog_article_id,
            2, -- 'Salida'
            2, -- 'Venta'
            oi.quantity,
            NEW.id,
            CONCAT('Venta - Orden #', NEW.order_number),
            NEW.user_id
        FROM order_item oi
        WHERE oi.order_id = NEW.id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_reduce_stock_on_order
    AFTER UPDATE OF order_status_id ON "order"
    FOR EACH ROW
    EXECUTE FUNCTION reduce_stock_on_order();

-- Function: Notify users when preorder becomes available
CREATE OR REPLACE FUNCTION notify_preorder_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.is_preorder = true AND NEW.is_preorder = false AND NEW.is_available = true THEN
        -- notification_type 'Preventa Disponible' has ID 1
        INSERT INTO user_notification (
            user_id,
            notification_type_id,
            title,
            message,
            reference_type,
            reference_id
        )
        SELECT 
            w.user_id,
            1, -- 'Preventa Disponible'
            'Preventa Disponible',
            CONCAT('El artículo "', NEW.title, '" ya está disponible para compra.'),
            'article',
            NEW.id
        FROM wishlist_item wi
        JOIN wishlist w ON wi.wishlist_id = w.id
        WHERE wi.analog_article_id = NEW.id;
        
        UPDATE wishlist_item 
        SET notification_sent = true
        WHERE analog_article_id = NEW.id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_notify_preorder_availability
    AFTER UPDATE ON analog_article
    FOR EACH ROW
    EXECUTE FUNCTION notify_preorder_availability();

-- Function: Apply cassette discount based on category
CREATE OR REPLACE FUNCTION apply_cassette_discount()
RETURNS TRIGGER AS $$
DECLARE
    base_price DECIMAL(10,2);
    discount_pct DECIMAL(5,2);
BEGIN
    IF EXISTS (SELECT 1 FROM cassette WHERE analog_article_id = NEW.analog_article_id) THEN
        
        SELECT price INTO base_price 
        FROM analog_article 
        WHERE id = NEW.analog_article_id;
        
        SELECT discount_percentage INTO discount_pct
        FROM cassette_category 
        WHERE id = NEW.cassette_category_id;
        
        IF discount_pct > 0 THEN
            UPDATE analog_article 
            SET price = base_price * (1 - discount_pct / 100),
                updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.analog_article_id;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_apply_cassette_discount
    AFTER INSERT OR UPDATE OF cassette_category_id ON cassette
    FOR EACH ROW
    EXECUTE FUNCTION apply_cassette_discount();

-- Function: Validate event participants limit
CREATE OR REPLACE FUNCTION validate_event_participants()
RETURNS TRIGGER AS $$
DECLARE
    max_participants INTEGER;
    current_count INTEGER;
BEGIN
    SELECT e.max_participants, e.current_participants 
    INTO max_participants, current_count
    FROM event e 
    WHERE e.id = NEW.event_id;
    
    IF max_participants IS NOT NULL AND current_count >= max_participants THEN
        RAISE EXCEPTION 'El evento ha alcanzado el máximo de participantes permitidos (%)', max_participants;
    END IF;
    
    UPDATE event 
    SET current_participants = current_participants + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.event_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_event_participants
    BEFORE INSERT ON event_registration
    FOR EACH ROW
    EXECUTE FUNCTION validate_event_participants();

-- Function: Decrement event participants when registration is cancelled
CREATE OR REPLACE FUNCTION decrement_event_participants()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE event 
    SET current_participants = GREATEST(current_participants - 1, 0),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = OLD.event_id;
    
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_decrement_event_participants
    AFTER DELETE ON event_registration
    FOR EACH ROW
    EXECUTE FUNCTION decrement_event_participants();

-- Function: Validate limited edition vinyl stock
CREATE OR REPLACE FUNCTION validate_limited_vinyl_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_limited_edition = true AND NEW.remaining_limited_stock IS NULL THEN
        RAISE EXCEPTION 'Los vinilos de edición limitada deben tener un stock específico definido';
    END IF;
    
    IF NEW.is_limited_edition = true AND NEW.remaining_limited_stock IS NOT NULL THEN
        IF NEW.remaining_limited_stock < 0 THEN
            RAISE EXCEPTION 'El stock de edición limitada no puede ser negativo';
        END IF;
        
        IF NEW.remaining_limited_stock = 0 THEN
            UPDATE analog_article 
            SET is_available = false,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.analog_article_id;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_limited_vinyl_stock
    BEFORE INSERT OR UPDATE ON vinyl
    FOR EACH ROW
    EXECUTE FUNCTION validate_limited_vinyl_stock();

-- Function: Validate stock movement reference requirements
CREATE OR REPLACE FUNCTION validate_stock_movement_reference()
RETURNS TRIGGER AS $$
DECLARE
    requires_ref BOOLEAN;
BEGIN
    SELECT requires_reference_id INTO requires_ref
    FROM movement_reference_type 
    WHERE id = NEW.movement_reference_type_id;
    
    IF requires_ref = true AND NEW.reference_id IS NULL THEN
        RAISE EXCEPTION 'Este tipo de movimiento requiere un ID de referencia';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_stock_movement_reference
    BEFORE INSERT OR UPDATE ON stock_movement
    FOR EACH ROW
    EXECUTE FUNCTION validate_stock_movement_reference();

-- =============================================
-- REPORTING FUNCTIONS
-- =============================================

-- Function: Get sales data grouped by period
CREATE OR REPLACE FUNCTION get_sales_by_period(
    start_date DATE,
    end_date DATE,
    group_by_period VARCHAR DEFAULT 'day'
)
RETURNS TABLE (
    period_label VARCHAR,
    total_sales DECIMAL(10,2),
    total_orders INTEGER,
    total_items INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        CASE 
            WHEN group_by_period = 'day' THEN TO_CHAR(o.created_at, 'YYYY-MM-DD')
            WHEN group_by_period = 'week' THEN TO_CHAR(DATE_TRUNC('week', o.created_at), 'YYYY-MM-DD')
            WHEN group_by_period = 'month' THEN TO_CHAR(o.created_at, 'YYYY-MM')
            WHEN group_by_period = 'year' THEN TO_CHAR(o.created_at, 'YYYY')
        END,
        SUM(o.total_amount),
        COUNT(o.id)::INTEGER,
        SUM(o.total_items)::INTEGER
    FROM "order" o
    WHERE o.created_at::DATE BETWEEN start_date AND end_date
    AND o.order_status_id = 4 -- 'Entregado'
    GROUP BY 1
    ORDER BY 1;
END;
$$ LANGUAGE plpgsql;

-- Function: Get top selling articles
CREATE OR REPLACE FUNCTION get_top_selling_articles(
    period_days INTEGER DEFAULT 30,
    limit_count INTEGER DEFAULT 10
)
RETURNS TABLE (
    article_id INTEGER,
    title VARCHAR,
    artist_name VARCHAR,
    article_type VARCHAR,
    total_sold INTEGER,
    total_revenue DECIMAL(10,2),
    average_rating DECIMAL(3,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        aa.id,
        aa.title,
        ar.name,
        CASE 
            WHEN v.id IS NOT NULL THEN 'vinyl'
            WHEN c.id IS NOT NULL THEN 'cassette'
            WHEN cd.id IS NOT NULL THEN 'cd'
        END,
        SUM(oi.quantity)::INTEGER,
        SUM(oi.total_price),
        aa.average_rating
    FROM analog_article aa
    JOIN artist ar ON aa.artist_id = ar.id
    LEFT JOIN vinyl v ON aa.id = v.analog_article_id
    LEFT JOIN cassette c ON aa.id = c.analog_article_id
    LEFT JOIN cd ON aa.id = cd.analog_article_id
    JOIN order_item oi ON aa.id = oi.analog_article_id
    JOIN "order" o ON oi.order_id = o.id
    WHERE o.created_at >= CURRENT_DATE - INTERVAL '1 day' * period_days
    AND o.order_status_id = 4 -- 'Entregado'
    GROUP BY aa.id, aa.title, ar.name, aa.average_rating, v.id, c.id, cd.id
    ORDER BY total_sold DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- PERFORMANCE INDEXES
-- =============================================

-- Indexes for improved trigger performance
CREATE INDEX IF NOT EXISTS idx_user_deleted_comments ON "user"(deleted_comments_count) WHERE deleted_comments_count > 0;
CREATE INDEX IF NOT EXISTS idx_analog_article_low_stock ON analog_article(id) WHERE stock_quantity <= min_stock_level;
CREATE INDEX IF NOT EXISTS idx_order_status_delivered ON "order"(created_at) WHERE order_status_id = 4;
CREATE INDEX IF NOT EXISTS idx_wishlist_item_notification ON wishlist_item(analog_article_id) WHERE notification_sent = false;
CREATE INDEX IF NOT EXISTS idx_event_participants ON event(current_participants, max_participants);
CREATE INDEX IF NOT EXISTS idx_vinyl_limited_edition ON vinyl(remaining_limited_stock) WHERE is_limited_edition = true;
CREATE INDEX IF NOT EXISTS idx_stock_movement_validation ON stock_movement(movement_reference_type_id, reference_id);
CREATE INDEX IF NOT EXISTS idx_comment_status_deleted ON article_comment(comment_status_id) WHERE comment_status_id = 2;
CREATE INDEX IF NOT EXISTS idx_order_status_processing ON "order"(order_status_id, user_id) WHERE order_status_id IN (1, 2);