INSERT INTO users (username, password, is_admin) 
SELECT 'admin', 'admin123', true
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

-- También podemos crear un usuario normal para pruebas
INSERT INTO users (username, password, is_admin) 
SELECT 'usuario1', 'usuario123', false
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'usuario1'
); 