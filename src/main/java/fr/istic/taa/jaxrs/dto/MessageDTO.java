package fr.istic.taa.jaxrs.dto;

import java.time.LocalDateTime;

import fr.istic.taa.jaxrs.dao.generic.AbstractJpaDao;
import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.entity.Message;

public class MessageDTO  {

	private Long id;
    private String title;
    private String content;
    private LocalDateTime dateSend;
    private Long userId;

    public MessageDTO() {}

    public MessageDTO(Long id, String title, String content, LocalDateTime dateSend, Long userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateSend = dateSend;
        this.userId = userId;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getDateSend() {
		return dateSend;
	}

	public void setDateSend(LocalDateTime dateSend) {
		//this.dateSend = dateSend;
		this.dateSend = (dateSend != null) ? dateSend : LocalDateTime.now();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
    
}
