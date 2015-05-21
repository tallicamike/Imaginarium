package com.example.android.imaginarium;

public class ClientFragment {
	private String text;
	private String authorName;
	private String media;

	public ClientFragment(String text, String authorName) {
		setText(text);
		setAuthorName(authorName);
	}
	public ClientFragment(String text, String authorName, String media) {
		setText(text);
		setAuthorName(authorName);
		setMedia(media);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}

    public ClientFragment() {}

}
