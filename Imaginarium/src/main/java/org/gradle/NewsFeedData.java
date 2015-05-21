package org.gradle;

import java.util.List;

public class NewsFeedData {
	private List<ClientStory> stories;

	public NewsFeedData(List<ClientStory> stories) {
		setStories(stories);
	}
	public List<ClientStory> getStories() {
		return stories;
	}

	public void setStories(List<ClientStory> stories) {
		this.stories = stories;
	}
	

}
