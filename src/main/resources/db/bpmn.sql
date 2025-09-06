-- User types catalog
CREATE TABLE user_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gender catalog
CREATE TABLE gender (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Currency catalog
CREATE TABLE currency (
    id SERIAL PRIMARY KEY,
    code CHAR(3) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    symbol VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Country catalog
CREATE TABLE country (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    country_code CHAR(2) UNIQUE,
    currency_id INTEGER REFERENCES currency(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Music genre catalog
CREATE TABLE music_genre (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vinyl category catalog (size)
CREATE TABLE vinyl_category (
    id SERIAL PRIMARY KEY,
    size VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    typical_rpm INTEGER DEFAULT 33,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vinyl special edition catalog
CREATE TABLE vinyl_special_edition (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(50),
    material_description TEXT,
    extra_content TEXT,
    is_limited BOOLEAN DEFAULT true,
    limited_quantity INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cassette category catalog (condition)
CREATE TABLE cassette_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Side type catalog
CREATE TABLE side_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order status catalog
CREATE TABLE order_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_final_status BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment method catalog
CREATE TABLE payment_method (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    requires_card BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Card brand catalog
CREATE TABLE card_brand (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment status catalog
CREATE TABLE payment_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_successful BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CD promotion type catalog
CREATE TABLE cd_promotion_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    max_items INTEGER NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL,
    is_time_limited BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Event status catalog
CREATE TABLE event_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    allows_registration BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Comment status catalog
CREATE TABLE comment_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_visible BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock movement type catalog
CREATE TABLE movement_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    affects_stock_increase BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock movement reference type catalog
CREATE TABLE movement_reference_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    requires_reference_id BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification type catalog
CREATE TABLE notification_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_email BOOLEAN DEFAULT true,
    is_system BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Artists table
CREATE TABLE artist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    biography TEXT,
    formation_date DATE,
    career_start_date DATE,
    country_id INTEGER REFERENCES country(id),
    is_band BOOLEAN DEFAULT false,
    website VARCHAR(255),
    total_sales INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender_id INTEGER REFERENCES gender(id),
    birth_date DATE,
    phone VARCHAR(20),
    user_type_id INTEGER NOT NULL REFERENCES user_type(id),
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    last_login TIMESTAMP,
    two_factor_code VARCHAR(6),
    two_factor_code_expires TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    is_2fa_enabled BOOLEAN DEFAULT false,
    deleted_comments_count INTEGER DEFAULT 0,
    is_banned BOOLEAN DEFAULT false,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    total_orders INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_deleted_comments CHECK (deleted_comments_count >= 0),
    CONSTRAINT chk_total_spent CHECK (total_spent >= 0)
);

-- Password reset tokens
CREATE TABLE password_reset_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT false,
    used_at TIMESTAMP,
    ip_address INET,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User addresses
CREATE TABLE user_address (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country_id INTEGER NOT NULL REFERENCES country(id),
    is_default BOOLEAN DEFAULT false,
    is_billing_default BOOLEAN DEFAULT false,
    is_shipping_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Credit cards (encrypted data)
CREATE TABLE credit_card (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    card_number_encrypted TEXT NOT NULL,
    cardholder_name_encrypted TEXT NOT NULL,
    expiry_month_encrypted TEXT NOT NULL,
    expiry_year_encrypted TEXT NOT NULL,
    cvv_encrypted TEXT NOT NULL,
    card_brand_id INTEGER NOT NULL REFERENCES card_brand(id),
    last_four_digits CHAR(4) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Main articles table (parent for all analog products)
CREATE TABLE analog_article (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist_id INTEGER NOT NULL REFERENCES artist(id),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    currency_id INTEGER NOT NULL REFERENCES currency(id),
    music_genre_id INTEGER NOT NULL REFERENCES music_genre(id),
    release_date DATE,
    description TEXT,
    dimensions VARCHAR(100),
    weight_grams INTEGER,
    barcode VARCHAR(50),
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    min_stock_level INTEGER DEFAULT 5,
    max_stock_level INTEGER DEFAULT 100,
    is_available BOOLEAN DEFAULT true,
    is_preorder BOOLEAN DEFAULT false,
    preorder_release_date DATE,
    preorder_end_date DATE,
    image_url VARCHAR(500),
    total_sold INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_ratings INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_preorder_dates CHECK (
        (is_preorder = false) OR 
        (is_preorder = true AND preorder_release_date IS NOT NULL)
    ),
    CONSTRAINT chk_rating CHECK (average_rating >= 0 AND average_rating <= 5)
);

-- Tracks table
CREATE TABLE track (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration_seconds INTEGER CHECK (duration_seconds > 0),
    track_number INTEGER CHECK (track_number > 0),
    composer VARCHAR(255),
    lyricist VARCHAR(255),
    isrc_code VARCHAR(15), -- International Standard Recording Code
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sides table (for vinyls and cassettes)
CREATE TABLE side (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    side_type_id INTEGER NOT NULL REFERENCES side_type(id),
    total_duration_seconds INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(analog_article_id, side_type_id)
);

-- Side tracks junction table (many-to-many)
CREATE TABLE side_track (
    side_id INTEGER NOT NULL REFERENCES side(id),
    track_id INTEGER NOT NULL REFERENCES track(id),
    position_order INTEGER NOT NULL CHECK (position_order > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (side_id, track_id),
    UNIQUE(side_id, position_order)
);

-- Vinyl specific table
CREATE TABLE vinyl (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL UNIQUE REFERENCES analog_article(id),
    vinyl_category_id INTEGER NOT NULL REFERENCES vinyl_category(id),
    vinyl_special_edition_id INTEGER REFERENCES vinyl_special_edition(id),
    rpm INTEGER DEFAULT 33 CHECK (rpm IN (33, 45)),
    is_limited_edition BOOLEAN DEFAULT false,
    remaining_limited_stock INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_limited_stock CHECK (
        (is_limited_edition = false) OR 
        (is_limited_edition = true AND remaining_limited_stock IS NOT NULL)
    )
);

-- Cassette specific table
CREATE TABLE cassette (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL UNIQUE REFERENCES analog_article(id),
    cassette_category_id INTEGER NOT NULL REFERENCES cassette_category(id),
    brand VARCHAR(100),
    is_chrome_tape BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CD specific table
CREATE TABLE cd (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL UNIQUE REFERENCES analog_article(id),
    disc_count INTEGER DEFAULT 1 CHECK (disc_count > 0),
    has_bonus_content BOOLEAN DEFAULT false,
    is_remastered BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CD promotions
CREATE TABLE cd_promotion (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cd_promotion_type_id INTEGER NOT NULL REFERENCES cd_promotion_type(id),
    music_genre_id INTEGER REFERENCES music_genre(id), -- Only for genre-based promotions
    discount_percentage DECIMAL(5,2) NOT NULL,
    max_items INTEGER NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP, -- NULL for indefinite promotions
    is_active BOOLEAN DEFAULT true,
    usage_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_promotion_dates CHECK (
        (end_date IS NULL) OR (end_date > start_date)
    )
);

-- CD promotion articles junction
CREATE TABLE cd_promotion_article (
    cd_promotion_id INTEGER NOT NULL REFERENCES cd_promotion(id),
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cd_promotion_id, analog_article_id)
);

-- Shopping cart
CREATE TABLE shopping_cart (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES "user"(id),
    total_items INTEGER DEFAULT 0,
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shopping cart items
CREATE TABLE shopping_cart_item (
    id SERIAL PRIMARY KEY,
    shopping_cart_id INTEGER NOT NULL REFERENCES shopping_cart(id),
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    discount_applied DECIMAL(10,2) DEFAULT 0.00,
    cd_promotion_id INTEGER REFERENCES cd_promotion(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shopping_cart_id, analog_article_id)
);

-- Orders
CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    order_status_id INTEGER NOT NULL REFERENCES order_status(id),
    currency_id INTEGER NOT NULL REFERENCES currency(id),
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    total_items INTEGER NOT NULL DEFAULT 0,
    shipping_address_id INTEGER NOT NULL REFERENCES user_address(id),
    billing_address_id INTEGER NOT NULL REFERENCES user_address(id),
    notes TEXT,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_order_totals CHECK (
        total_amount = subtotal + tax_amount + shipping_cost - discount_amount
    )
);

-- Order items
CREATE TABLE order_item (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES "order"(id),
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(10,2) NOT NULL,
    cd_promotion_id INTEGER REFERENCES cd_promotion(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_item_total CHECK (
        total_price = (unit_price * quantity) - discount_amount
    )
);

-- Payments
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    payment_number VARCHAR(50) NOT NULL UNIQUE,
    order_id INTEGER NOT NULL REFERENCES "order"(id),
    payment_method_id INTEGER NOT NULL REFERENCES payment_method(id),
    payment_status_id INTEGER NOT NULL REFERENCES payment_status(id),
    currency_id INTEGER NOT NULL REFERENCES currency(id),
    amount DECIMAL(10,2) NOT NULL,
    transaction_reference VARCHAR(255),
    gateway_response TEXT,
    gateway_transaction_id VARCHAR(255),
    processed_at TIMESTAMP,
    refunded_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices
CREATE TABLE invoice (
    id SERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    order_id INTEGER NOT NULL UNIQUE REFERENCES "order"(id),
    currency_id INTEGER NOT NULL REFERENCES currency(id),
    issue_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE,
    tax_id VARCHAR(50), -- Customer tax ID
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    notes TEXT,
    pdf_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Wishlist
CREATE TABLE wishlist (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES "user"(id),
    total_items INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Wishlist items
CREATE TABLE wishlist_item (
    id SERIAL PRIMARY KEY,
    wishlist_id INTEGER NOT NULL REFERENCES wishlist(id),
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    is_preorder_paid BOOLEAN DEFAULT false,
    preorder_payment_id INTEGER REFERENCES payment(id),
    notification_sent BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(wishlist_id, analog_article_id)
);

-- Preorder audio files (preliminary listening for preorders)
CREATE TABLE preorder_audio (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    audio_file_url VARCHAR(500) NOT NULL,
    track_title VARCHAR(255),
    duration_seconds INTEGER,
    file_size_bytes BIGINT,
    download_count INTEGER DEFAULT 0,
    is_downloadable BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User preorder audio access (who can access which preview)
CREATE TABLE user_preorder_audio_access (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    preorder_audio_id INTEGER NOT NULL REFERENCES preorder_audio(id),
    access_granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_played_at TIMESTAMP,
    play_count INTEGER DEFAULT 0,
    downloaded BOOLEAN DEFAULT false,
    downloaded_at TIMESTAMP,
    PRIMARY KEY (user_id, preorder_audio_id)
);

-- Events
CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_status_id INTEGER NOT NULL REFERENCES event_status(id),
    analog_article_id INTEGER REFERENCES analog_article(id),
    audio_file_url VARCHAR(500) NOT NULL,
    audio_duration_seconds INTEGER NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    allow_chat BOOLEAN DEFAULT true,
    created_by_user_id INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_event_dates CHECK (end_datetime > start_datetime),
    CONSTRAINT chk_event_participants CHECK (
        (max_participants IS NULL) OR 
        (current_participants <= max_participants)
    )
);

-- Event registrations
CREATE TABLE event_registration (
    event_id INTEGER NOT NULL REFERENCES event(id),
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    attended BOOLEAN DEFAULT false,
    attendance_duration_seconds INTEGER DEFAULT 0,
    PRIMARY KEY (event_id, user_id)
);

-- Event chat messages
CREATE TABLE event_chat_message (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES event(id),
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    message TEXT NOT NULL,
    is_system_message BOOLEAN DEFAULT false,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Article ratings
CREATE TABLE article_rating (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    is_verified_purchase BOOLEAN DEFAULT false,
    helpful_votes INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(analog_article_id, user_id)
);

-- Article comments
CREATE TABLE article_comment (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    parent_comment_id INTEGER REFERENCES article_comment(id),
    comment_text TEXT NOT NULL,
    comment_status_id INTEGER NOT NULL REFERENCES comment_status(id),
    deleted_by_user_id INTEGER REFERENCES "user"(id),
    deleted_reason TEXT,
    deleted_at TIMESTAMP,
    likes_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock movements log
CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    analog_article_id INTEGER NOT NULL REFERENCES analog_article(id),
    movement_type_id INTEGER NOT NULL REFERENCES movement_type(id),
    movement_reference_type_id INTEGER NOT NULL REFERENCES movement_reference_type(id),
    quantity INTEGER NOT NULL,
    previous_stock INTEGER NOT NULL,
    new_stock INTEGER NOT NULL,
    reference_id INTEGER,
    notes TEXT,
    created_by_user_id INTEGER REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stock_movement CHECK (
        (movement_reference_type_id IN (
            SELECT id FROM movement_reference_type WHERE requires_reference_id = false
        )) OR 
        (reference_id IS NOT NULL)
    )
);

-- User notifications
CREATE TABLE user_notification (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    notification_type_id INTEGER NOT NULL REFERENCES notification_type(id),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(50), -- 'order', 'event', 'preorder', etc.
    reference_id INTEGER,
    is_read BOOLEAN DEFAULT false,
    is_email_sent BOOLEAN DEFAULT false,
    email_sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- User indexes
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_username ON "user"(username);
CREATE INDEX idx_user_type_id ON "user"(user_type_id);
CREATE INDEX idx_user_active ON "user"(is_active);
CREATE INDEX idx_user_banned ON "user"(is_banned);

-- Article indexes
CREATE INDEX idx_analog_article_artist ON analog_article(artist_id);
CREATE INDEX idx_analog_article_genre ON analog_article(music_genre_id);
CREATE INDEX idx_analog_article_available ON analog_article(is_available);
CREATE INDEX idx_analog_article_preorder ON analog_article(is_preorder);
CREATE INDEX idx_analog_article_stock ON analog_article(stock_quantity);
CREATE INDEX idx_analog_article_rating ON analog_article(average_rating);
CREATE INDEX idx_analog_article_sales ON analog_article(total_sold);
CREATE INDEX idx_analog_article_price ON analog_article(price);

-- Order indexes
CREATE INDEX idx_order_user ON "order"(user_id);
CREATE INDEX idx_order_status ON "order"(order_status_id);
CREATE INDEX idx_order_date ON "order"(created_at);
CREATE INDEX idx_order_number ON "order"(order_number);
CREATE INDEX idx_order_total ON "order"(total_amount);

-- Event indexes
CREATE INDEX idx_event_datetime ON event(start_datetime);
CREATE INDEX idx_event_status ON event(event_status_id);
CREATE INDEX idx_event_article ON event(analog_article_id);

-- Comment indexes
CREATE INDEX idx_article_comment_article ON article_comment(analog_article_id);
CREATE INDEX idx_article_comment_user ON article_comment(user_id);
CREATE INDEX idx_article_comment_status ON article_comment(comment_status_id);

-- Rating indexes
CREATE INDEX idx_article_rating_article ON article_rating(analog_article_id);
CREATE INDEX idx_article_rating_user ON article_rating(user_id);
CREATE INDEX idx_article_rating_value ON article_rating(rating);

-- Stock movement indexes
CREATE INDEX idx_stock_movement_article ON stock_movement(analog_article_id);
CREATE INDEX idx_stock_movement_type ON stock_movement(movement_type_id);
CREATE INDEX idx_stock_movement_date ON stock_movement(created_at);

-- Notification indexes
CREATE INDEX idx_notification_user ON user_notification(user_id);
CREATE INDEX idx_notification_unread ON user_notification(user_id, is_read);
CREATE INDEX idx_notification_type ON user_notification(notification_type_id);

-- =============================================
-- INITIAL CATALOG DATA (IN SPANISH)
-- =============================================

-- Currencies
INSERT INTO currency (code, name, symbol) VALUES 
('GTQ', 'Quetzal Guatemalteco', 'Q'),
('USD', 'DÃ³lar Estadounidense', '$'),
('EUR', 'Euro', 'â‚¬');

-- User types
INSERT INTO user_type (name, description) VALUES 
('Cliente', 'Usuario cliente que puede comprar productos'),
('Administrador', 'Usuario administrador con acceso completo al sistema');

-- Gender
INSERT INTO gender (name, description) VALUES 
('Masculino', 'GÃ©nero masculino'),
('Femenino', 'GÃ©nero femenino'),
('Otro', 'Otro gÃ©nero');

-- Order status
INSERT INTO order_status (name, description, is_final_status) VALUES 
('Pendiente', 'Orden creada pero no procesada', false),
('Procesando', 'Orden en proceso de preparaciÃ³n', false),
('Enviado', 'Orden enviada al cliente', false),
('Entregado', 'Orden entregada exitosamente', true),
('Cancelado', 'Orden cancelada', true);

-- Payment methods
INSERT INTO payment_method (name, description, requires_card) VALUES 
('Tarjeta de CrÃ©dito', 'Pago con tarjeta de crÃ©dito', true),
('Tarjeta de DÃ©bito', 'Pago con tarjeta de dÃ©bito', true),
('Transferencia Bancaria', 'Pago por transferencia bancaria', false);

-- Card brands
INSERT INTO card_brand (name) VALUES 
('Visa'),
('MasterCard'),
('American Express'),
('Discover');

-- Payment status
INSERT INTO payment_status (name, description, is_successful) VALUES 
('Pendiente', 'Pago pendiente de procesamiento', false),
('Completado', 'Pago procesado exitosamente', true),
('Fallido', 'Pago fallÃ³ durante el procesamiento', false),
('Reembolsado', 'Pago reembolsado al cliente', false);

-- Vinyl categories
INSERT INTO vinyl_category (size, description, typical_rpm) VALUES 
('7 pulgadas', 'Formato pequeÃ±o para sencillos', 45),
('10 pulgadas', 'Formato medio para EPs', 33),
('12 pulgadas', 'Formato estÃ¡ndar para LPs', 33);

-- Cassette categories with discounts
INSERT INTO cassette_category (name, discount_percentage, description) VALUES 
('Nuevo', 0.00, 'Cassette en condiciÃ³n nueva'),
('Semi Usado', 20.00, 'Cassette con uso mÃ­nimo'),
('Usado', 50.00, 'Cassette con uso considerable');

-- Side types
INSERT INTO side_type (name, description) VALUES 
('Lado A', 'Primera cara de vinilo o cassette'),
('Lado B', 'Segunda cara de vinilo o cassette'),
('CD', 'Disco compacto'),
('Cassette A', 'Primera cara del cassette'),
('Cassette B', 'Segunda cara del cassette');

-- CD promotion types
INSERT INTO cd_promotion_type (name, max_items, discount_percentage, is_time_limited, description) VALUES 
('Por GÃ©nero', 4, 10.00, true, 'PromociÃ³n de CDs agrupados por gÃ©nero musical'),
('Aleatorio', 7, 30.00, false, 'PromociÃ³n de CDs agrupados aleatoriamente');

-- Event status
INSERT INTO event_status (name, description, allows_registration) VALUES 
('Programado', 'Evento programado para fecha futura', true),
('En Curso', 'Evento actualmente en desarrollo', false),
('Finalizado', 'Evento completado', false),
('Cancelado', 'Evento cancelado', false);

-- Comment status
INSERT INTO comment_status (name, description, is_visible) VALUES 
('Activo', 'Comentario visible y activo', true),
('Eliminado', 'Comentario eliminado por moderaciÃ³n', false),
('Reportado', 'Comentario reportado por usuarios', true);

-- Movement types
INSERT INTO movement_type (name, description, affects_stock_increase) VALUES 
('Entrada', 'Ingreso de mercancÃ­a al inventario', true),
('Salida', 'Salida de mercancÃ­a del inventario', false),
('Ajuste', 'Ajuste de inventario por correcciÃ³n', true);

-- Movement reference types
INSERT INTO movement_reference_type (name, description, requires_reference_id) VALUES 
('Compra', 'Entrada por compra a proveedor', true),
('Venta', 'Salida por venta a cliente', true),
('DaÃ±o', 'Salida por mercancÃ­a daÃ±ada', false),
('Reconteo', 'Ajuste por reconteo de inventario', false),
('DevoluciÃ³n', 'Entrada por devoluciÃ³n de cliente', true);

-- Notification types
INSERT INTO notification_type (name, description, is_email, is_system) VALUES 
('Preventa Disponible', 'NotificaciÃ³n cuando un artÃ­culo en preventa estÃ¡ disponible', true, true),
('Evento PrÃ³ximo', 'Recordatorio de evento prÃ³ximo', true, true),
('Orden Procesada', 'ConfirmaciÃ³n de procesamiento de orden', true, true),
('Stock Bajo', 'Alerta de stock bajo para administradores', true, false),
('Comentario Eliminado', 'NotificaciÃ³n de comentario eliminado', false, true);

-- Basic music genres
INSERT INTO music_genre (name, description) VALUES 
('Rock', 'GÃ©nero musical rock'),
('Pop', 'GÃ©nero musical pop'),
('Jazz', 'GÃ©nero musical jazz'),
('Blues', 'GÃ©nero musical blues'),
('ClÃ¡sica', 'MÃºsica clÃ¡sica'),
('ElectrÃ³nica', 'MÃºsica electrÃ³nica'),
('Hip Hop', 'GÃ©nero hip hop'),
('Reggae', 'GÃ©nero reggae'),
('Folk', 'MÃºsica folk'),
('Metal', 'GÃ©nero metal'),
('ReggaetÃ³n', 'GÃ©nero reggaetÃ³n'),
('Salsa', 'GÃ©nero salsa'),
('Merengue', 'GÃ©nero merengue'),
('Bachata', 'GÃ©nero bachata');

-- Basic countries with currencies
INSERT INTO country (name, country_code, currency_id) VALUES 
('Guatemala', 'GT', (SELECT id FROM currency WHERE code = 'GTQ')),
('Estados Unidos', 'US', (SELECT id FROM currency WHERE code = 'USD')),
('MÃ©xico', 'MX', (SELECT id FROM currency WHERE code = 'USD')),
('EspaÃ±a', 'ES', (SELECT id FROM currency WHERE code = 'EUR')),
('Reino Unido', 'GB', (SELECT id FROM currency WHERE code = 'USD')),
('Alemania', 'DE', (SELECT id FROM currency WHERE code = 'EUR')),
('Francia', 'FR', (SELECT id FROM currency WHERE code = 'EUR')),
('Italia', 'IT', (SELECT id FROM currency WHERE code = 'EUR')),
('JapÃ³n', 'JP', (SELECT id FROM currency WHERE code = 'USD')),
('Brasil', 'BR', (SELECT id FROM currency WHERE code = 'USD'));

-- Basic vinyl special editions
INSERT INTO vinyl_special_edition (name, color, extra_content, is_limited, limited_quantity) VALUES 
('EdiciÃ³n EstÃ¡ndar', 'Negro', NULL, false, NULL),
('EdiciÃ³n Transparente', 'Transparente', 'Vinilo transparente', true, 500),
('EdiciÃ³n Dorada', 'Dorado', 'Vinilo dorado con pÃ³ster', true, 250),
('EdiciÃ³n de Colores', 'Multicolor', 'Vinilo multicolor salpicado', true, 300),
('EdiciÃ³n Limitada Blanca', 'Blanco', 'Vinilo blanco con booklet', true, 200);