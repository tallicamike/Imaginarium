package org.gradle;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



public class DBtest {
	private static SessionFactory factory;


	public DBtest() {
		 try{
			 Configuration configuration = new Configuration();
			
             configuration.configure("org/gradle/hibernate.cfg.xml"); 
	         factory = configuration.buildSessionFactory();
	      }catch (Throwable ex) { 
	         System.err.println("Failed to create sessionFactory object." + ex);
	         throw new ExceptionInInitializerError(ex); 
	      }
	}
	
	public int createUser(String gid, String gname) {
		Session session = factory.openSession();
		Transaction tx = null;
		int ret = -1;
		Query query = session.createQuery("FROM User where gid = :gid");
		query.setParameter("gid", gid);
		List usrs = query.list();
		if(usrs.size() != 0) {
			System.out.println("user already in database\n");
			return -1; /* e-mail already used */
		}
		
		try {
			tx = session.beginTransaction();
			User usr = new User(gid, gname);
			session.save(usr);
			tx.commit();
			ret = 0;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			ret = -1;
		} finally {
			session.close();
			return ret;
		}
		
	}
	
	public int createStory(String gid, String title, String description) {
		Session session = factory.openSession();
		Transaction tx = null;
		int ret = -1;
		int myStoryId;
		List<Integer> ids;
		Query query = session.createQuery("FROM User where gid = :gid");
		query.setParameter("gid", gid);
		
		List usrs = query.list();
		if(usrs.size()==0)
			return -1;
		Story story= new Story(title, description, null, false);
		Fragment rootUser=new Fragment((User)usrs.get(0), story, new String("@@@"));
		Participant part = new Participant((User)usrs.get(0),rootUser);
		try {
			tx = session.beginTransaction();
			session.save(story);
			session.save(rootUser);
			session.save(part);
			tx.commit();
			ret = 0;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			Query qMyStory = session.createSQLQuery("Select id_story from Fragment where id = (Select max(id_fragment) from Participant where id_user = (select id from User where gid =:gid))");
			qMyStory.setParameter("gid", gid);
			ids = qMyStory.list();
			if(ids.size() == 0) {
				myStoryId = -1;
			} else {
				myStoryId = ids.get(0);
			}
			session.close();
			return myStoryId;
		}
		
	}
	
	public int addFragment(String gid, int storyId, String text, String name) {
		Session session = factory.openSession();
		Transaction tx = null;
		int ret = -1;
		Query queryUsr = session.createQuery("FROM User where gid = :gid");
		queryUsr.setParameter("gid", gid);
		List<User> usrs = queryUsr.list();
		
		if(usrs.size()==0)
			return -1; /* user not in database : sanity check */
		
		Query queryStory = session.createQuery("From Story where id = :sid");
		queryStory.setParameter("sid",storyId);
		List<Story> story = queryStory.list();
		
		if(story.size()==0)
			return -1;
		
		Fragment newFrag = new Fragment(usrs.get(0), story.get(0), text, name);
		Participant newPart = new Participant(usrs.get(0), newFrag);
		try {
			tx = session.beginTransaction();
			session.save(newFrag);
			session.save(newPart);
			tx.commit();
			ret = 0;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			ret = -1;
		} finally {
			session.close();
			return ret;
		}

	}
	
	public int getTimeUnits(int storyId, String units, Session session) {
		
		Query qLastUpdate = session.createSQLQuery("SELECT TIMESTAMPDIFF("+units+",(SELECT last_update from Story where id = :sid),NOW())");
		List<BigInteger> timeUnits;
		qLastUpdate.setParameter("sid", storyId);
		timeUnits = qLastUpdate.list();
		if(timeUnits.size() ==0) {
			System.out.println("Unnable to get time of last update from db");
			return -1;
		}
		return timeUnits.get(0).intValue();
	}
	
	public String getRightTime(int storyId) {
		Session session = factory.openSession();
		int timeUnits;
		if((timeUnits = getTimeUnits(storyId,"DAY", session))>0)
			return new String("Last updated "+timeUnits+" days ago");
		if((timeUnits = getTimeUnits(storyId,"HOUR", session))>0)
			return new String("Last updated "+timeUnits+" hours ago");
		if((timeUnits = getTimeUnits(storyId,"MINUTE", session))>0)
			return new String("Last updated "+timeUnits+" minutes ago");
		if((timeUnits = getTimeUnits(storyId,"SECOND", session))>0)
			return new String("Last updated "+timeUnits+" seconds ago");
		return new String("Just now...");
	}
	public ClientStory getStory(int storyId) {
		List<Fragment> frags;
		ClientStory story;
		List<Story> titles;
		

		
		Session session = factory.openSession();
		Query qTitle = session.createQuery("From Story where id = :sid");
		qTitle.setParameter("sid", storyId);
		titles=qTitle.list();
		if(titles.size()==0) {
			System.out.println("Ghost story! No title");
			return null;
		}
		Query qStory = session.createQuery("FROM Fragment where id_story = :sid");
		qStory.setParameter("sid", storyId);
		frags = qStory.list();
		if(frags.size() == 0) {
			System.out.println("Ghost Story!");
			return null; /* should have at leas one fragment -> story author */
		}
		

		
		story = new ClientStory(storyId, frags.get(0).getUser().getGname(),titles.get(0).getTitle(), getRightTime(storyId)); /* TODO: just name */
		for(Fragment dbFrag:frags.subList(1,frags.size())) {
			story.addFragment(new ClientFragment(dbFrag.getText(), dbFrag.getUser().getGname(),dbFrag.getMedia())); /* TODO: just name */
		}
		return story;
	}
	
	
	public List<ClientStory> getStories(String gid,String []friends) {
		Session session = factory.openSession();
		int idToAdd;
		int n = 0;
		boolean isNewStory;
		List<Story> stories;
		List<User> userFriends = new ArrayList<User>();
		List<Participant> participantFriends = new ArrayList<Participant>();
		List<ClientStory> clntStories = new ArrayList<ClientStory>();
		int [] storiesSoFar = new int[10000];
		int ret = -1;
		/*
		for(int i=0; i<friends.length; i++) {
			Query query = session.createQuery("FROM User where gid = :my_gid or gid = :friend_gid");
			query.setParameter("my_gid", gid);
			query.setParameter("friend_gid", friends[i]);
			userFriends.addAll((List<User>)query.list());
		}
		for(User usr:userFriends) {
			//Query query = session.createQuery("select p FROM Participant p where  p.id_story IN (select distinct e.id_story from Participant e where e.id_user=:idu)");
			Query query = session.createQuery("From Participant where id_user= :idu");
			query.setParameter("idu", usr.getId());
			participantFriends.addAll((List<Participant>)query.list());
		}
		for(Participant part:participantFriends) {
			idToAdd = part.getFragment().getStory().getId();
			isNewStory = true;
			for(int i =0;i<n;i++) {
				if(idToAdd==storiesSoFar[i]) {
					isNewStory = false;
					break;
				}
			}
			if(isNewStory) {
				clntStories.add(getStory(idToAdd));
				storiesSoFar[n++] = idToAdd;
			}
		}
		*/
		Query qStories = session.createQuery("FROM Story");
		stories = qStories.list();
		for(Story s:stories)
			clntStories.add(getStory(s.getId()));
		return clntStories;
	} 

}
