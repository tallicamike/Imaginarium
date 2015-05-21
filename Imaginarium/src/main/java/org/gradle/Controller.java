package org.gradle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Controller {
	DBtest db = new DBtest();
	/*public Controller() {
		db = new DBtest(); 
	}*/
	@RequestMapping(value="/test_ping", method = RequestMethod.GET)
	public void testPing() {
		
	}
	@RequestMapping(value = "/create_user", method = RequestMethod.GET)
	public int createUsr(@RequestParam(value="gid") String gid,
			@RequestParam(value="gname") String gname) {
		return db.createUser(gid, gname);
		
	}
	@RequestMapping(value ="/user_friends", method = RequestMethod.GET)
	public String queryStories(@RequestParam(value="gid") String user, @RequestParam(value="friends")String friends) {
		String[] friend= friends.split(",");
		
		return new String("");
	}
	@RequestMapping(value ="/create_story", method = RequestMethod.GET)
	public Integer createStory(@RequestParam(value="gid") String gid, @RequestParam(value="title")String title,
								@RequestParam(value="description") String description) {
		return new Integer(db.createStory(gid, title, description));
			
	}
	@RequestMapping(value ="/continue_story", method = RequestMethod.GET)
	public int continueStory(@RequestParam(value="gid") String gid, @RequestParam(value="story_id") int storyId,
							@RequestParam(value="text") String text/*,@RequestParam("media") MultipartFile file*/) {
		String name = null;
		/*
		if (!file.isEmpty()) {
            try {
            	
            	Random generator = new Random();
                byte[] bytes = file.getBytes();
                name = generator.nextInt(1000)+file.getOriginalFilename();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("Web/"+name)));
                stream.write(bytes);
                stream.close();
                System.out.println("You successfully uploaded " + name + "!");
            } catch (Exception e) {
                System.out.println("You failed to upload  but you tried something" + e.getMessage());
            }
        } else {
            System.out.println("You failed to upload because the file was empty.");
        }
        */
		return db.addFragment(gid, storyId, text, name);
	}
	@RequestMapping(value="/get_story", method = RequestMethod.GET)
	public ClientStory getStory(@RequestParam(value="story_id")int storyId) {
		return db.getStory(storyId);
	}
	
	@RequestMapping(value="/get_friends_stories", method = RequestMethod.GET)
	public NewsFeedData getFriendsStories(@RequestParam(value="gid") String gid, @RequestParam(value="friends") String friendsIds) {
		String [] splittedIds = friendsIds.split(",");
		
		return new NewsFeedData(db.getStories(gid, splittedIds));
	}
	
	
}
