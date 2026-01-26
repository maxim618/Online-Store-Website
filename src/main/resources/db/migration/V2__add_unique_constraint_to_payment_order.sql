ALTER TABLE payments
ADD CONSTRAINT uk_payment_order_id UNIQUE (order_id);