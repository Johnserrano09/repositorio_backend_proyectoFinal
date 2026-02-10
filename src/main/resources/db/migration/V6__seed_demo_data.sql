-- V6__seed_demo_data.sql
-- Demo data for testing

-- Admin user
INSERT INTO users (id, email, name, phone, bio, role, is_active) VALUES
('11111111-1111-1111-1111-111111111111', 'admin@portfolio.com', 'Administrador Sistema', '+1234567890', 'Administrador del sistema de portafolios', 'ADMIN', true);

-- Programmer users
INSERT INTO users (id, email, name, phone, bio, role, is_active) VALUES
('22222222-2222-2222-2222-222222222222', 'dev1@portfolio.com', 'Juan Developer', '+1234567891', 'Desarrollador Full Stack con 5 años de experiencia en Java y Angular. Especialista en arquitectura de microservicios.', 'PROGRAMMER', true),
('33333333-3333-3333-3333-333333333333', 'dev2@portfolio.com', 'María Coder', '+1234567892', 'Desarrolladora Frontend especializada en React y Angular. Apasionada por UX/UI y accesibilidad web.', 'PROGRAMMER', true);

-- External users
INSERT INTO users (id, email, name, phone, bio, role, is_active) VALUES
('44444444-4444-4444-4444-444444444444', 'user1@portfolio.com', 'Carlos Cliente', '+1234567893', 'Emprendedor buscando desarrolladores para proyectos.', 'EXTERNAL', true),
('55555555-5555-5555-5555-555555555555', 'user2@portfolio.com', 'Ana Solicitante', '+1234567894', 'Gerente de proyectos en empresa de tecnología.', 'EXTERNAL', true);

-- Projects for Juan Developer
INSERT INTO projects (id, user_id, title, description, project_type, role_in_project, technologies, repo_url, demo_url, status) VALUES
('aaaa1111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'Sistema de Inventarios', 'Sistema completo de gestión de inventarios con control de stock, alertas y reportes', 'WORK', 'BACKEND', ARRAY['Java', 'Spring Boot', 'PostgreSQL', 'Docker'], 'https://github.com/juan/inventarios', 'https://inventarios-demo.com', 'COMPLETED'),
('aaaa2222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'App de Delivery', 'Aplicación móvil para servicio de delivery con tracking en tiempo real', 'WORK', 'FULLSTACK', ARRAY['Angular', 'Node.js', 'MongoDB', 'Firebase'], 'https://github.com/juan/delivery', 'https://delivery-demo.com', 'COMPLETED'),
('aaaa3333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'Tesis: ML para Predicción', 'Sistema de machine learning para predicción de ventas usando redes neuronales', 'ACADEMIC', 'FULLSTACK', ARRAY['Python', 'TensorFlow', 'Flask', 'React'], 'https://github.com/juan/tesis-ml', NULL, 'COMPLETED');

-- Projects for María Coder
INSERT INTO projects (id, user_id, title, description, project_type, role_in_project, technologies, repo_url, demo_url, status) VALUES
('bbbb1111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 'Dashboard Analytics', 'Dashboard interactivo para visualización de datos empresariales', 'WORK', 'FRONTEND', ARRAY['Angular', 'Chart.js', 'TailwindCSS', 'RxJS'], 'https://github.com/maria/dashboard', 'https://dashboard-demo.com', 'COMPLETED'),
('bbbb2222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'E-commerce Platform', 'Plataforma de comercio electrónico con carrito y pagos integrados', 'WORK', 'FRONTEND', ARRAY['React', 'Redux', 'Stripe', 'Material-UI'], 'https://github.com/maria/ecommerce', 'https://shop-demo.com', 'IN_PROGRESS'),
('bbbb3333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Proyecto Final: SPA Accesible', 'Single Page Application con enfoque en accesibilidad WCAG 2.1', 'ACADEMIC', 'FRONTEND', ARRAY['Vue.js', 'Vuetify', 'Jest', 'Cypress'], 'https://github.com/maria/spa-accesible', 'https://spa-accesible.com', 'COMPLETED');

-- Availability for Juan Developer
INSERT INTO availability (user_id, day_of_week, start_time, end_time, is_active) VALUES
('22222222-2222-2222-2222-222222222222', 'MONDAY', '09:00', '12:00', true),
('22222222-2222-2222-2222-222222222222', 'MONDAY', '14:00', '18:00', true),
('22222222-2222-2222-2222-222222222222', 'WEDNESDAY', '09:00', '12:00', true),
('22222222-2222-2222-2222-222222222222', 'FRIDAY', '10:00', '14:00', true);

-- Availability for María Coder
INSERT INTO availability (user_id, day_of_week, start_time, end_time, is_active) VALUES
('33333333-3333-3333-3333-333333333333', 'TUESDAY', '08:00', '12:00', true),
('33333333-3333-3333-3333-333333333333', 'THURSDAY', '14:00', '18:00', true),
('33333333-3333-3333-3333-333333333333', 'SATURDAY', '10:00', '13:00', true);

-- Sample advisories
INSERT INTO advisories (id, programmer_id, external_id, scheduled_at, status, request_comment, response_message) VALUES
('cccc1111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', '2024-02-15 10:00:00', 'APPROVED', 'Necesito ayuda con arquitectura de microservicios', 'Perfecto, agendado!'),
('cccc2222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', '2024-02-16 14:00:00', 'PENDING', 'Consulta sobre Spring Security y JWT', NULL),
('cccc3333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', '44444444-4444-4444-4444-444444444444', '2024-02-14 08:00:00', 'COMPLETED', 'Revisión de UI/UX para mi proyecto', 'Fue un gusto ayudarte!'),
('cccc4444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', '2024-02-13 14:00:00', 'REJECTED', 'Ayuda con React hooks', 'Lo siento, esa fecha no me es posible. Por favor agenda otro día.');

-- Sample notifications
INSERT INTO notification_log (user_id, type, destination, subject, payload, status, sent_at) VALUES
('22222222-2222-2222-2222-222222222222', 'EMAIL', 'dev1@portfolio.com', 'Nueva solicitud de asesoría', '{"advisoryId": "cccc1111-1111-1111-1111-111111111111", "from": "Carlos Cliente"}', 'SENT', '2024-02-10 10:00:00'),
('44444444-4444-4444-4444-444444444444', 'EMAIL', 'user1@portfolio.com', 'Tu asesoría fue aprobada', '{"advisoryId": "cccc1111-1111-1111-1111-111111111111", "programmer": "Juan Developer"}', 'SENT', '2024-02-10 11:00:00'),
('44444444-4444-4444-4444-444444444444', 'WHATSAPP', '+1234567893', 'Recordatorio de asesoría', '{"message": "Tu asesoría con Juan Developer es mañana a las 10:00", "advisoryId": "cccc1111-1111-1111-1111-111111111111"}', 'SENT', '2024-02-14 10:00:00');
