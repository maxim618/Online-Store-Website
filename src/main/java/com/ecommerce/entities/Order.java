package com.ecommerce.entities;

import java.time.LocalDateTime;

public class Order {
	private int id;
	private String orderId;
	private String status;
	private LocalDateTime date;   // 👈 современный тип вместо Timestamp
	private String paymentType;   // 👈 одно поле, без дубля
	private int userId;

	public Order() {
	}

	public Order(String orderId, String status, LocalDateTime date, String paymentType, int userId) {
		this.orderId = orderId;
		this.status = status;
		this.date = date;
		this.paymentType = paymentType;
		this.userId = userId;
	}

	public Order(String orderId, String status, String paymentType, int userId) {
		this(orderId, status, LocalDateTime.now(), paymentType, userId); // 👈 дата по умолчанию = now()
	}

	// getters / setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
