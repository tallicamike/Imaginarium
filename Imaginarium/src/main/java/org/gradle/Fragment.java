// default package
// Generated May 20, 2015 1:19:23 PM by Hibernate Tools 3.6.0
package org.gradle;
import java.util.HashSet;
import java.util.Set;

/**
 * Fragment generated by hbm2java
 */
public class Fragment implements java.io.Serializable {

	private Integer id;
	private User user;
	private Story story;
	private String text;
	private String media;
	private Set participants = new HashSet(0);

	public Fragment() {
	}

	public Fragment(User user, Story story, String text) {
		this.user = user;
		this.story = story;
		this.text = text;
		this.media = null; /*TODO */
	}
	public Fragment(User user, Story story, String text, String media) {
		this.user = user;
		this.story = story;
		this.text = text;
		this.media = media; /*TODO */
	}

	public Fragment(User user, Story story, String text, String media,
			Set participants) {
		this.user = user;
		this.story = story;
		this.text = text;
		this.media = media;
		this.participants = participants;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Story getStory() {
		return this.story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMedia() {
		return this.media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public Set getParticipants() {
		return this.participants;
	}

	public void setParticipants(Set participants) {
		this.participants = participants;
	}

}