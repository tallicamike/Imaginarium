package com.example.android.imaginarium;

import java.util.ArrayList;
import java.util.List;

public class ClientStory {
	private int storyId;
	private String authorName;
	private String title;
	private String lastUpdate;
	private List<ClientFragment> frags;


	public ClientStory(int storyId, String authorName, String title, String lastUpdate) {
		setStoryId(storyId);
		setAuthorName(authorName);
		frags = new ArrayList<ClientFragment>();
		this.lastUpdate = lastUpdate;
		this.title = title;
	}
	public ClientStory(int storyId, String authorName, String title, List<ClientFragment> frags, String lastUpdate)
	{
		this.storyId=storyId;
		this.authorName=authorName;
		this.title =title;
		this.frags=frags;
	}
	public int getStoryId() {
		return storyId;
	}
	public void setStoryId(int storyId) {
		this.storyId = storyId;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public void addFragment(ClientFragment frag) {
		frags.add(frag);
	}
	public List<ClientFragment> getFragments() {
		return frags;
	}
	public void setFragments(List<ClientFragment> frags) {
		this.frags = frags;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

    public ClientStory() {}

}
