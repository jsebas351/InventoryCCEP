CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    state VARCHAR(10)
);

CREATE TABLE subcategories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    category_id INT REFERENCES categories(id),
    state VARCHAR(10)
);

CREATE TABLE suppliers (
    id SERIAL PRIMARY KEY,
    nit BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone BIGINT,
    mail VARCHAR(100) NOT NULL,
    state VARCHAR(10)
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    reference BIGINT NOT NULL,
    description VARCHAR(250) NOT NULL,
    purchase_price BIGINT NOT NULL,
    sale_price BIGINT NOT NULL,
    subcategory_id INT NOT NULL REFERENCES subcategories(id),
    provider_id INT NOT NULL REFERENCES suppliers(id),
    state VARCHAR(10)
);

CREATE TABLE payments_methods (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20),
    state VARCHAR(10)
);

CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    total_sale BIGINT NOT NULL,
    sale_date DATE NOT NULL,
    edit_date DATE,
    discount BIGINT NOT NULL,
    user_id INT NOT NULL,
    paymethod_id INT NOT NULL REFERENCES payments_methods(id),
    state VARCHAR(10)
);

CREATE TABLE sales_details (
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    subtotal BIGINT NOT NULL,
    product_id INT NOT NULL REFERENCES products(id),
    discount_product BIGINT NOT NULL,
    sale_id INT NOT NULL REFERENCES sales(id)
);

CREATE TABLE purchases (
    id SERIAL PRIMARY KEY,
    total_purchase BIGINT NOT NULL,
    bill_number BIGINT NOT NULL,
    purchase_date DATE NOT NULL,
    edit_date DATE,
    provider_id INT NOT NULL REFERENCES suppliers(id),
    state VARCHAR(10)
);

CREATE TABLE purchases_details (
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    subtotal BIGINT NOT NULL,
    product_id INT NOT NULL REFERENCES products(id),
    purchase_id INT NOT NULL REFERENCES purchases(id)
);

CREATE TABLE entries (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(id),
    quantity BIGINT NOT NULL,
    dateEntry DATE NOT NULL,
    edit_date DATE
);

CREATE TABLE losses (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(id),
    quantity BIGINT NOT NULL,
    description TEXT NOT NULL,
    dateLoss DATE NOT NULL,
    edit_date DATE
);

CREATE TABLE inventories (
    id SERIAL PRIMARY KEY,
    stock BIGINT NOT NULL,
    product_id INT NOT NULL REFERENCES products(id),
    saledetail_id INT REFERENCES sales_details(id),
    purchasedetail_id INT REFERENCES purchases_details(id),
	entry_id INT REFERENCES entries(id),
	loss_id INT REFERENCES losses(id)
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name_role VARCHAR(100)
);

CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    second_name VARCHAR(255),
    first_last_name VARCHAR(255),
    second_last_name VARCHAR(255),
    email VARCHAR(255),
    phone BIGINT,
    identification BIGINT,
    type_identification VARCHAR(255)
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    password_encrypted VARCHAR(255),
    person_id INT REFERENCES persons(id),
    state VARCHAR(10)
);

CREATE TABLE users_roles (
    user_id INT REFERENCES users(id),
    rol_id INT REFERENCES roles(id),
    PRIMARY KEY (user_id, rol_id)
);