package com.ecommerce.entities;

public class Message {
	private String content;   // Основной текст сообщения
	private String title;     // Заголовок (например "Success", "Error")
	private String type;      // Bootstrap класс: alert-success, alert-danger, alert-warning

	public Message(String content, String title, String type) {
		this.content = content;
		this.title = title;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
