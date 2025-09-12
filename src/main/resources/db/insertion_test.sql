INSERT INTO artist (name, biography, formation_date, career_start_date, country_id, is_band, website, total_sales) VALUES 
('The Beatles', 'Banda británica de rock formada en Liverpool en 1960. Considerados como la banda más influyente de todos los tiempos.', '1960-08-17', '1957-07-06', 2, true, 'https://www.thebeatles.com', 150000),
('Miles Davis', 'Trompetista, flugelhornista, pianista y compositor estadounidense de jazz. Una de las figuras más influyentes e innovadoras de la música del siglo XX.', NULL, '1945-11-26', 2, false, 'https://www.milesdavis.com', 85000),
('Pink Floyd', 'Banda británica de rock progresivo formada en Londres en 1965. Pioneros en el uso de conceptos filosóficos y musicales complejos.', '1965-01-01', '1965-01-01', 5, true, 'https://www.pinkfloyd.com', 200000);

-- =============================================
-- TEST USERS (3 users: 1 admin + 2 customers)
-- =============================================

INSERT INTO "user" (username, email, first_name, last_name, gender_id, birth_date, phone, user_type_id, password_hash, is_active, is_verified, two_factor_code, two_factor_code_expires, is_2fa_enabled) VALUES 
('admin', 'dj_maldonado19@hotmail.es', 'Administrator', 'System', 1, '1980-01-15', '502-1234-5678', 2, '$2a$10$example.hash.admin.password.encrypted', true, true, NULL, NULL, false),
('johndoe', 'dmaldonado@cari.net', 'John', 'Doe', 1, '1990-05-15', '502-2345-6789', 1, '$2a$10$example.hash.john.password.encrypted', true, true, '123456', '2024-12-31 23:59:59', true),
('janesmith', 'jane.smith@email.com', 'Jane', 'Smith', 2, '1985-08-22', '502-3456-7890', 1, '$2a$10$example.hash.jane.password.encrypted', true, true, NULL, NULL, false);

-- =============================================
-- USER ADDRESSES (3 addresses for testing)
-- =============================================

INSERT INTO user_address (user_id, address_line_1, address_line_2, city, state, postal_code, country_id, is_default, is_billing_default, is_shipping_default) VALUES 
(2, 'Zona 10, Edificio Torre Internacional', 'Oficina 15A', 'Guatemala', 'Guatemala', '01010', 1, true, true, true),
(2, 'Avenida Las Américas 5-67', 'Apto 23B', 'Guatemala', 'Guatemala', '01011', 1, false, false, false),
(3, 'Boulevard Los Próceres 18-40', 'Casa 25', 'Guatemala', 'Guatemala', '01012', 1, true, true, true);

-- =============================================
-- CREDIT CARDS (3 test cards)
-- =============================================

INSERT INTO credit_card (user_id, card_number_encrypted, cardholder_name_encrypted, expiry_month_encrypted, expiry_year_encrypted, cvv_encrypted, card_brand_id, last_four_digits, is_default) VALUES 
(2, 'encrypted_4532123456789012', 'encrypted_JOHN_DOE', 'encrypted_12', 'encrypted_27', 'encrypted_123', 1, '9012', true),
(2, 'encrypted_5123456789012345', 'encrypted_JOHN_DOE', 'encrypted_06', 'encrypted_26', 'encrypted_456', 2, '2345', false),
(3, 'encrypted_4111111111111111', 'encrypted_JANE_SMITH', 'encrypted_09', 'encrypted_28', 'encrypted_789', 1, '1111', true);

-- =============================================
-- ANALOG ARTICLES (9 articles: 3 vinyl, 3 cassette, 3 CD)
-- =============================================

-- VINYL ARTICLES (3)
INSERT INTO analog_article (title, artist_id, price, currency_id, music_genre_id, release_date, description, dimensions, weight_grams, barcode, stock_quantity, min_stock_level, max_stock_level, is_available, image_url, total_sold, average_rating, total_ratings) VALUES 
('Abbey Road', 1, 450.00, 1, 1, '1969-09-26', 'Álbum icónico de The Beatles lanzado en 1969. Considerado una obra maestra del rock.', '30.5 x 30.5 x 0.5 cm', 180, '602537271863', 25, 5, 50, true, 'https://example.com/abbey-road.jpg', 120, 4.8, 45),
('Kind of Blue', 2, 520.00, 1, 3, '1959-08-17', 'Obra maestra del jazz modal de Miles Davis. Uno de los álbumes de jazz más influyentes de todos los tiempos.', '30.5 x 30.5 x 0.5 cm', 180, '886972408125', 15, 3, 30, true, 'https://example.com/kind-of-blue.jpg', 85, 4.9, 32),
('The Dark Side of the Moon', 3, 600.00, 1, 1, '1973-03-01', 'Álbum conceptual de Pink Floyd de 1973. Una exploración de temas como la locura, la muerte y el tiempo.', '30.5 x 30.5 x 0.5 cm', 180, '5099902893112', 30, 8, 60, true, 'https://example.com/dark-side-moon.jpg', 200, 4.7, 67);

-- CASSETTE ARTICLES (3)
INSERT INTO analog_article (title, artist_id, price, currency_id, music_genre_id, release_date, description, dimensions, weight_grams, barcode, stock_quantity, min_stock_level, max_stock_level, is_available, image_url, total_sold, average_rating, total_ratings) VALUES 
('Revolver', 1, 180.00, 1, 1, '1966-08-05', 'Séptimo álbum de estudio de The Beatles. Marca una evolución hacia sonidos más experimentales.', '10.8 x 6.8 x 1.5 cm', 30, '077774642125', 40, 10, 80, true, 'https://example.com/revolver-cassette.jpg', 60, 4.6, 25),
('Bitches Brew', 2, 200.00, 1, 3, '1970-03-30', 'Álbum que marca la transición de Miles Davis hacia el jazz fusion.', '10.8 x 6.8 x 1.5 cm', 32, '088691222221', 20, 5, 40, true, 'https://example.com/bitches-brew-cassette.jpg', 40, 4.5, 18),
('Wish You Were Here', 3, 220.00, 1, 1, '1975-09-12', 'Noveno álbum de estudio de Pink Floyd, tributo a Syd Barrett.', '10.8 x 6.8 x 1.5 cm', 28, '509991266125', 35, 8, 70, true, 'https://example.com/wish-you-were-here-cassette.jpg', 75, 4.8, 30);

-- CD ARTICLES (3)
INSERT INTO analog_article (title, artist_id, price, currency_id, music_genre_id, release_date, description, dimensions, weight_grams, barcode, stock_quantity, min_stock_level, max_stock_level, is_available, image_url, total_sold, average_rating, total_ratings) VALUES 
('Sgt. Peppers Lonely Hearts Club Band', 1, 280.00, 1, 1, '1967-06-01', 'Octavo álbum de estudio de The Beatles. Considerado uno de los mejores álbumes de todos los tiempos.', '12.4 x 14.2 x 1.0 cm', 15, '077774644823', 50, 12, 100, true, 'https://example.com/sgt-peppers-cd.jpg', 180, 4.9, 55),
('Birth of the Cool', 2, 320.00, 1, 3, '1957-02-01', 'Compilación de sesiones de grabación que definen el cool jazz.', '12.4 x 14.2 x 1.0 cm', 15, '888751231225', 30, 8, 60, true, 'https://example.com/birth-cool-cd.jpg', 95, 4.7, 28),
('Animals', 3, 250.00, 1, 1, '1977-01-23', 'Décimo álbum de estudio de Pink Floyd. Crítica social basada en "Rebelión en la granja" de Orwell.', '12.4 x 14.2 x 1.0 cm', 15, '509991077725', 25, 6, 50, true, 'https://example.com/animals-cd.jpg', 110, 4.6, 35);

-- =============================================
-- ARTICLE TYPE SPECIFIC DATA
-- =============================================

-- VINYL RECORDS (using existing catalog IDs)
INSERT INTO vinyl (analog_article_id, vinyl_category_id, vinyl_special_edition_id, rpm, is_limited_edition, remaining_limited_stock) VALUES 
(1, 3, 1, 33, false, NULL),              -- Abbey Road: 12" Standard Black
(2, 3, 2, 33, true, 50),                 -- Kind of Blue: 12" Transparent Limited
(3, 3, 3, 33, true, 25);                 -- Dark Side: 12" Gold Limited

-- CASSETTES (using existing cassette categories)
INSERT INTO cassette (analog_article_id, cassette_category_id, brand, is_chrome_tape) VALUES 
(4, 1, 'TDK', false),                    -- Revolver: New condition, TDK brand
(5, 2, 'Maxell', true),                  -- Bitches Brew: Semi Used, Chrome tape
(6, 1, 'Sony', false);                   -- Wish You Were Here: New condition

-- CDS
INSERT INTO cd (analog_article_id, disc_count, has_bonus_content, is_remastered) VALUES 
(7, 1, true, true),                      -- Sgt. Peppers: Remastered with bonus
(8, 1, false, true),                     -- Birth of Cool: Remastered only
(9, 1, false, false);                    -- Animals: Standard edition

-- =============================================
-- TRACKS AND SIDES
-- =============================================

-- TRACKS (sample tracks for testing)
INSERT INTO track (title, duration_seconds, track_number, composer, lyricist) VALUES 
-- Abbey Road tracks
('Come Together', 260, 1, 'John Lennon', 'John Lennon'),
('Something', 183, 2, 'George Harrison', 'George Harrison'),
('Maxwell''s Silver Hammer', 207, 3, 'Paul McCartney', 'Paul McCartney'),
-- Kind of Blue tracks  
('So What', 562, 1, 'Miles Davis', NULL),
('Freddie Freeloader', 576, 2, 'Miles Davis', NULL),
('Blue in Green', 337, 3, 'Miles Davis', NULL),
-- Dark Side tracks
('Speak to Me', 90, 1, 'Nick Mason', NULL),
('Breathe', 163, 2, 'Roger Waters', 'Roger Waters'),
('On the Run', 216, 3, 'David Gilmour', NULL);

-- SIDES (for vinyl/cassette articles)
INSERT INTO side (analog_article_id, side_type_id, total_duration_seconds) VALUES 
-- Abbey Road sides (using side_type: 1=Lado A, 2=Lado B)
(1, 1, 1233), -- Abbey Road Lado A
(1, 2, 1108), -- Abbey Road Lado B
-- Kind of Blue sides
(2, 1, 1138), -- Kind of Blue Lado A
(2, 2, 1075), -- Kind of Blue Lado B
-- Revolver cassette sides (using side_type: 4=Cassette A, 5=Cassette B)
(4, 4, 890),  -- Revolver Cassette A
(4, 5, 845);  -- Revolver Cassette B

-- SIDE-TRACK RELATIONSHIPS
INSERT INTO side_track (side_id, track_id, position_order) VALUES 
-- Abbey Road Lado A
(1, 1, 1), (1, 2, 2), (1, 3, 3),
-- Kind of Blue Lado A  
(3, 4, 1), (3, 5, 2),
-- Kind of Blue Lado B
(4, 6, 1);

-- =============================================
-- SHOPPING CARTS
-- =============================================

INSERT INTO shopping_cart (user_id, total_items, subtotal) VALUES 
(2, 2, 970.00),  -- John's cart: 2 items
(3, 1, 600.00);  -- Jane's cart: 1 item

INSERT INTO shopping_cart_item (shopping_cart_id, analog_article_id, quantity, unit_price, discount_applied) VALUES 
(1, 1, 1, 450.00, 0.00),   -- John: Abbey Road vinyl
(1, 2, 1, 520.00, 0.00),   -- John: Kind of Blue vinyl
(2, 3, 1, 600.00, 0.00);   -- Jane: Dark Side vinyl

-- =============================================
-- ORDERS (3 orders in different statuses)
-- =============================================

INSERT INTO "order" (order_number, user_id, order_status_id, currency_id, subtotal, tax_amount, discount_amount, shipping_cost, total_amount, total_items, shipping_address_id, billing_address_id, notes) VALUES 
('ORD-20241201-001', 2, 4, 1, 450.00, 54.00, 0.00, 25.00, 529.00, 1, 1, 1, 'Entrega urgente solicitada'),      -- Delivered
('ORD-20241202-002', 3, 2, 1, 520.00, 62.40, 26.00, 25.00, 581.40, 1, 3, 3, 'Regalo para cumpleaños'),        -- Processing  
('ORD-20241203-003', 2, 1, 1, 600.00, 72.00, 0.00, 25.00, 697.00, 1, 1, 1, NULL);                             -- Pending

-- ORDER ITEMS
INSERT INTO order_item (order_id, analog_article_id, quantity, unit_price, discount_amount, total_price) VALUES 
(1, 1, 1, 450.00, 0.00, 450.00),    -- Order 1: Abbey Road
(2, 2, 1, 520.00, 26.00, 494.00),   -- Order 2: Kind of Blue with discount
(3, 3, 1, 600.00, 0.00, 600.00);    -- Order 3: Dark Side

-- =============================================
-- PAYMENTS
-- =============================================

INSERT INTO payment (payment_number, order_id, payment_method_id, payment_status_id, currency_id, amount, transaction_reference, processed_at) VALUES 
('PAY-20241201-001', 1, 1, 2, 1, 529.00, 'TXN123456789', '2024-12-01 15:30:00'),    -- Completed payment
('PAY-20241202-002', 2, 2, 2, 1, 581.40, 'PP987654321', '2024-12-02 10:45:00'),     -- Completed payment  
('PAY-20241203-003', 3, 1, 1, 1, 697.00, 'TXN456789123', NULL);                     -- Pending payment

-- =============================================
-- INVOICES
-- =============================================

INSERT INTO invoice (invoice_number, order_id, currency_id, issue_date, subtotal, tax_amount, total_amount, notes) VALUES 
('INV-20241201-001', 1, 1, '2024-12-01', 450.00, 54.00, 529.00, 'Factura generada automáticamente'),
('INV-20241202-002', 2, 1, '2024-12-02', 520.00, 62.40, 581.40, 'Promoción de descuento aplicada'),
('INV-20241203-003', 3, 1, '2024-12-03', 600.00, 72.00, 697.00, 'Factura pendiente de pago');

-- =============================================
-- CD PROMOTIONS (using existing promotion types)
-- =============================================

INSERT INTO cd_promotion (name, cd_promotion_type_id, music_genre_id, discount_percentage, max_items, start_date, end_date, is_active) VALUES 
('Promoción Jazz Clásico', 1, 3, 10.00, 4, '2024-01-01 00:00:00', '2024-03-31 23:59:59', true),    -- Genre-based: Jazz
('Liquidación Aleatoria', 2, NULL, 30.00, 7, '2024-01-15 00:00:00', NULL, true),                   -- Random promotion
('Flash Rock Sale', 1, 1, 15.00, 3, '2024-02-01 00:00:00', '2024-02-15 23:59:59', true);          -- Genre-based: Rock

INSERT INTO cd_promotion_article (cd_promotion_id, analog_article_id) VALUES 
(1, 8),  -- Jazz promotion: Birth of the Cool
(2, 9),  -- Random promotion: Animals  
(3, 7);  -- Rock promotion: Sgt. Peppers

-- =============================================
-- EVENTS  
-- =============================================

INSERT INTO event (title, description, event_status_id, analog_article_id, audio_file_url, audio_duration_seconds, start_datetime, end_datetime, max_participants, current_participants, allow_chat, created_by_user_id) VALUES 
('Concierto Beatles Tribute', 'Noche especial dedicada a The Beatles con reproducción en vivo de Abbey Road', 1, 1, 'https://example.com/audio/beatles-event.mp3', 3600, '2024-06-15 20:00:00', '2024-06-15 23:00:00', 100, 25, true, 1),
('Jazz Night Miles Davis', 'Velada de jazz en honor a Miles Davis reproduciendo Kind of Blue', 1, 2, 'https://example.com/audio/jazz-event.mp3', 4200, '2024-07-20 19:30:00', '2024-07-20 22:30:00', 80, 15, true, 1),
('Pink Floyd Experience', 'Experiencia inmersiva de Pink Floyd con Dark Side of the Moon', 2, 3, 'https://example.com/audio/pink-floyd-event.mp3', 5400, '2024-05-30 21:00:00', '2024-05-31 01:00:00', 150, 145, true, 1);

-- EVENT REGISTRATIONS
INSERT INTO event_registration (event_id, user_id, attended, attendance_duration_seconds) VALUES 
(1, 2, false, 0),    -- John registered for Beatles event
(1, 3, false, 0),    -- Jane registered for Beatles event  
(2, 2, false, 0);    -- John registered for Jazz event

-- =============================================
-- WISHLISTS
-- =============================================

INSERT INTO wishlist (user_id, total_items) VALUES 
(2, 2),  -- John's wishlist: 2 items
(3, 1);  -- Jane's wishlist: 1 item

INSERT INTO wishlist_item (wishlist_id, analog_article_id, is_preorder_paid, notification_sent) VALUES 
(1, 4, false, false),  -- John wants Revolver cassette
(1, 7, false, true),   -- John wants Sgt. Peppers CD (notified)
(2, 6, false, false);  -- Jane wants Wish You Were Here cassette

-- =============================================
-- PREORDER AUDIO
-- =============================================

INSERT INTO preorder_audio (analog_article_id, audio_file_url, track_title, duration_seconds, file_size_bytes, download_count, is_downloadable) VALUES 
(1, 'https://example.com/preview/abbey-road-preview.mp3', 'Come Together (Preview)', 30, 1048576, 45, true),
(2, 'https://example.com/preview/kind-of-blue-preview.mp3', 'So What (Preview)', 45, 1572864, 32, true),
(3, 'https://example.com/preview/dark-side-preview.mp3', 'Breathe (Preview)', 60, 2097152, 67, true);

-- USER PREORDER AUDIO ACCESS
INSERT INTO user_preorder_audio_access (user_id, preorder_audio_id, last_played_at, play_count, downloaded, downloaded_at) VALUES 
(2, 1, '2024-12-01 14:30:00', 3, true, '2024-12-01 14:35:00'),
(2, 2, '2024-12-02 16:15:00', 1, false, NULL),
(3, 3, '2024-12-03 10:20:00', 5, true, '2024-12-03 10:25:00');

-- =============================================
-- ARTICLE RATINGS
-- =============================================

INSERT INTO article_rating (analog_article_id, user_id, rating, review_text, is_verified_purchase, helpful_votes) VALUES 
(1, 2, 5, 'Excelente calidad de sonido y presentación impecable. Totalmente recomendado para cualquier fanático de The Beatles.', true, 8),
(1, 3, 4, 'Muy buen producto, aunque el empaque llegó un poco dañado. La música suena fantástica.', false, 3),
(2, 3, 5, 'Una obra maestra del jazz. La remasterización es extraordinaria y se escucha cada detalle.', false, 12);

-- =============================================
-- ARTICLE COMMENTS (using existing comment status IDs)
-- =============================================

INSERT INTO article_comment (analog_article_id, user_id, parent_comment_id, comment_text, comment_status_id, likes_count) VALUES 
(1, 2, NULL, '¿Alguien sabe si esta edición incluye material adicional o es solo el álbum original?', 1, 5),      -- Active comment
(1, 3, 1, 'Sí, incluye fotos inéditas de las sesiones de grabación y notas del productor George Martin.', 1, 3),   -- Reply to comment 1
(2, 3, NULL, 'Miles Davis revolucionó completamente el jazz con este álbum. Una experiencia auditiva única.', 1, 8); -- Active comment

-- =============================================
-- STOCK MOVEMENTS (using existing movement type IDs)
-- =============================================

INSERT INTO stock_movement (analog_article_id, movement_type_id, movement_reference_type_id, quantity, previous_stock, new_stock, reference_id, notes, created_by_user_id) VALUES 
(1, 1, 4, 50, 0, 50, NULL, 'Stock inicial Abbey Road - Ingreso por reconteo', 1),        -- Entry movement
(2, 1, 4, 30, 0, 30, NULL, 'Stock inicial Kind of Blue - Ingreso por reconteo', 1),      -- Entry movement  
(3, 1, 4, 40, 0, 40, NULL, 'Stock inicial Dark Side - Ingreso por reconteo', 1),         -- Entry movement
(1, 2, 2, 1, 50, 49, 1, 'Venta orden ORD-20241201-001', NULL),                          -- Exit movement for sale
(2, 2, 2, 1, 30, 29, 2, 'Venta orden ORD-20241202-002', NULL);                          -- Exit movement for sale

-- =============================================
-- USER NOTIFICATIONS (using existing notification type IDs)
-- =============================================

INSERT INTO user_notification (user_id, notification_type_id, title, message, reference_type, reference_id, is_read, email_sent_at) VALUES 
(2, 1, 'Preventa Disponible - Abbey Road', 'El artículo "Abbey Road" que tienes en tu wishlist ahora está disponible para preventa.', 'preorder', 1, true, '2024-12-01 10:00:00'),
(2, 3, 'Orden Procesada', 'Tu orden ORD-20241201-001 ha sido procesada exitosamente y está en preparación.', 'order', 1, true, '2024-12-01 14:30:00'),
(3, 1, 'Preventa Disponible - Kind of Blue', 'El artículo "Kind of Blue" que te interesa ahora está en preventa.', 'preorder', 2, false, NULL),
(3, 3, 'Orden Procesada', 'Tu orden ORD-20241202-002 ha sido procesada exitosamente.', 'order', 2, false, '2024-12-02 16:45:00'),
(1, 4, 'Stock Bajo - Abbey Road', 'El artículo "Abbey Road" tiene stock bajo. Stock actual: 25, Mínimo: 5', 'article', 1, false, '2024-12-03 08:00:00');

-- =============================================
-- PASSWORD RESET TOKENS (for testing auth endpoints)
-- =============================================

INSERT INTO password_reset_token (token, user_id, expires_at, used, ip_address) VALUES 
('test-reset-token-123456', 2, '2024-12-31 23:59:59', false, '192.168.1.100'),
('test-reset-token-789012', 3, '2024-12-31 23:59:59', true, '192.168.1.101'),
('test-reset-token-345678', 2, '2024-12-31 23:59:59', false, '192.168.1.102');

-- =============================================
-- EVENT CHAT MESSAGES
-- =============================================

INSERT INTO event_chat_message (event_id, user_id, message, is_system_message) VALUES 
(1, 1, 'Bienvenidos al evento Beatles Tribute. ¡Esperamos que disfruten!', true),
(1, 2, '¡Qué emocionante! Abbey Road es mi álbum favorito de The Beatles.', false),
(1, 3, 'La calidad del audio es increíble. ¡Gracias por organizar este evento!', false),
(2, 1, 'Iniciamos la reproducción de Kind of Blue. ¡Disfruten esta obra maestra!', true),
(2, 2, 'Miles Davis era un genio. Este álbum cambió mi vida.', false);

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Count all test data inserted
SELECT 'Artists' as entity, COUNT(*) as count FROM artist
UNION ALL SELECT 'Users', COUNT(*) FROM "user"  
UNION ALL SELECT 'Articles', COUNT(*) FROM analog_article
UNION ALL SELECT 'Vinyl Records', COUNT(*) FROM vinyl
UNION ALL SELECT 'Cassettes', COUNT(*) FROM cassette  
UNION ALL SELECT 'CDs', COUNT(*) FROM cd
UNION ALL SELECT 'Orders', COUNT(*) FROM "order"
UNION ALL SELECT 'Payments', COUNT(*) FROM payment
UNION ALL SELECT 'Events', COUNT(*) FROM event
UNION ALL SELECT 'Promotions', COUNT(*) FROM cd_promotion
UNION ALL SELECT 'Ratings', COUNT(*) FROM article_rating
UNION ALL SELECT 'Comments', COUNT(*) FROM article_comment
UNION ALL SELECT 'Stock Movements', COUNT(*) FROM stock_movement
UNION ALL SELECT 'Notifications', COUNT(*) FROM user_notification
ORDER BY entity;