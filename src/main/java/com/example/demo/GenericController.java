package com.example.demo;



import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.tree.RowMapper;



@Controller
class genericCtrl{
	
@Autowired
UserRepository userrepo;

@Autowired
TeamsRepo teamsrepo;

 @GetMapping("/index")
 public String renderIndex(){
   
     return "index";
 }
 

 @GetMapping("/mainpage")
 public ModelAndView rendermainpage(HttpServletRequest request ){

	 
	ModelAndView mainpage = new ModelAndView();
	Selectfaveteams teams = new Selectfaveteams();
	HttpSession session = request.getSession();
    Object userid = session.getAttribute("userId");
    String user = (String) userid;
    Long number = Long.parseLong(user);
    Optional<User> useropt = null;	
	useropt = userrepo.findById(number);
	User username = useropt.get();
	try {
	    username = useropt.get();
    } catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
    
    
    
    Optional<Selectfaveteams> n = null;
	n = teamsrepo.findById(number);
	teams = n.get();   
    System.out.println("teams inka last time"+teams.getTeamslist());
   
    if(user != null) {
    	
    	teamsrepo.findById(Long.parseLong(user));
    	teams.getTeamslist();
    }
    
//////////////////////////////////////////////////////////////////////////////    
    
    
	ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
	
	
	  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyddMM");
	  LocalDateTime now = LocalDateTime.now();
	  System.out.println("Checking Data"+dtf.format(now));
	  String x = dtf.format(now);
	
	
	String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/scoreboard.json?fordate=20181112";
	
	String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
 
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.set("Authorization", "Basic "+encoding);
	HttpEntity<String> request1 = new HttpEntity<String>(headers);

	
	
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request1, String.class);
	String str = response.getBody(); 
	ObjectMapper mapper = new ObjectMapper();
	try {
		JsonNode root = mapper.readTree(str);
		System.out.println(str);
		//JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
      System.out.println(root.get("scoreboard").get("lastUpdatedOn").asText());
      System.out.println(root.get("scoreboard").get("gameScore").getNodeType());
      JsonNode gameScore = root.get("scoreboard").get("gameScore");
      
      if(gameScore.isArray()) {
      	
      	gameScore.forEach(gamescr -> {
      		JsonNode game = gamescr.get("game");
      		HashMap<String,String> gameDetail = new HashMap<String, String>();
      	
      		gameDetail.put("homeTeam", game.get("homeTeam").get("Name").asText());
      		gameDetail.put("awayTeam", game.get("awayTeam").get("Name").asText());
      		gameDetail.put("homeScore",gamescr.get("homeScore").asText());
      		gameDetail.put("awayScore",gamescr.get("awayScore").asText());
      		//gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
      		gameDetails.add(gameDetail);
      		
      	});
      }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	mainpage.addObject("gameDetails", gameDetails);
	mainpage.addObject("UserName", username.getName());
    
/////////////////////////////////////////////////////////////////////////////////    
    mainpage.setViewName("mainpage");
    mainpage.addObject("selteams",teams.getTeamslist());
    return mainpage;
 }
 

 
private String getResponseQuery(HttpSession session) {
	// TODO Auto-generated method stub
	return null;
}

///////////Api integration//////////////// 
 @GetMapping("/teams")
 public ModelAndView mainpage(HttpSession session) {
	//String userId = session.getAttribute("userId").toString();
		
		
		ModelAndView showTeams = new ModelAndView();
		showTeams.addObject("name", "Human"); 
		
		//Endpoint to call
		String url ="https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
		//Encode Username and Password
        String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<NBATeamStanding> response = restTemplate.exchange(url, HttpMethod.GET, request, NBATeamStanding.class);
		NBATeamStanding ts = response.getBody(); 
        System.out.println(ts.toString());
		//Send the object to view
        showTeams.addObject("teamStandingEntries", ts.getOverallteamstandings().getTeamstandingsentries());
        showTeams.setViewName("showteams");
		//return showTeams;
		return showTeams;
	
	}
	 
@GetMapping("/blocked")
	public ModelAndView blocked(HttpSession session){
		
		ModelAndView blocked = new ModelAndView();
		blocked.setViewName("blocked");
	    return blocked;
		
	} 
 
 
 
//Using objectMapper
	@GetMapping("/team")
	public ModelAndView getTeamInfo(
			@RequestParam("id") String teamID 
			) {
		ModelAndView teamInfo = new ModelAndView("teaminfo");
		ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/team_gamelogs.json?team=" + teamID;
		String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
       
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody(); 
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			//JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
	        System.out.println(root.get("teamgamelogs").get("lastUpdatedOn").asText());
	        System.out.println(root.get("teamgamelogs").get("gamelogs").getNodeType());
	        JsonNode gamelogs = root.get("teamgamelogs").get("gamelogs");
	        
	        if(gamelogs.isArray()) {
	        	
	        	gamelogs.forEach(gamelog -> {
	        		JsonNode game = gamelog.get("game");
	        		JsonNode stats = gamelog.get("stats");
	        		HashMap<String,String> gameDetail = new HashMap<String, String>();
	        		gameDetail.put("id", game.get("id").asText());
	        		gameDetail.put("date", game.get("date").asText());
	        		gameDetail.put("time", game.get("time").asText());
	        		gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
	        		gameDetail.put("Location", game.get("location").asText());
	        		gameDetail.put("Wins",   stats.get("Wins").get("#text").asText());
	        		gameDetail.put("Losses", stats.get("Losses").get("#text").asText());
	        		gameDetails.add(gameDetail);
	        		
	        	});
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		teamInfo.addObject("gameDetails", gameDetails);
		return teamInfo;
	}
 
 
	@GetMapping("/adminpage")
	public ModelAndView admininfo(HttpSession session){
		
		ModelAndView admininfo = new ModelAndView();
		
		List<User> users = new ArrayList<User>();
		for(User user : userrepo.findAll()) {
			users.add(user);
		}	
		System.out.print(userrepo.findAll());
		admininfo.addObject("users", userrepo.findAll());
		//if(userid.equals("115712049459208")) {
		admininfo.setViewName("adminpage");
	    return admininfo;
		
	}
	

	@PostMapping(path="/mainpage") // Map ONLY Post Requests
	/*public ModelAndView addNewUser (@RequestParam String userId
			, @RequestParam String userName) {*/
	public String addNewUser (@RequestParam String userId, 
			                  @RequestParam String userName, HttpServletRequest request) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		
		ModelAndView mv = new ModelAndView();	
	    HttpSession session = request.getSession();
	    session.setAttribute("userId", userId);		
		
		
/////////////////////////////////////////////////////////////////////////////////////////////////		
		
		ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();	
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/scoreboard.json?fordate=20181112";
		
		String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request1 = new HttpEntity<String>(headers);
		
		
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request1, String.class);
		String str = response.getBody(); 
		ObjectMapper mapper = new ObjectMapper();
		try {
		JsonNode root = mapper.readTree(str);
		JsonNode gameScore = root.get("scoreboard").get("gameScore");
		
		if(gameScore.isArray()) {
		
		gameScore.forEach(gamescr -> {
		JsonNode game = gamescr.get("game");
		HashMap<String,String> gameDetail = new HashMap<String, String>();
		
		gameDetail.put("homeTeam", game.get("homeTeam").get("Name").asText());
		gameDetail.put("awayTeam", game.get("awayTeam").get("Name").asText());
		gameDetail.put("homeScore",gamescr.get("homeScore").asText());
		gameDetail.put("awayScore",gamescr.get("awayScore").asText());
		//gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
		gameDetails.add(gameDetail);
		
		});
		}
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
			
		
			
/////////////////////////////////////////////////////////////////////////////////////////
			
			

				
		Long number = Long.parseLong(userId);
		Optional<User> n = null;
		n = userrepo.findById(number);
		User n1 = new User();
		System.out.print(n);
		
		try {
			    n1 = n.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if(n1.getId() == null) {
			
			n1.setId(number);
			n1.setName(userName);
			n1.setBlocked(false);
			userrepo.save(n1);
			
			mv.addObject("UserName", userName);
			mv.addObject("gameDetails", gameDetails);
			request.setAttribute("UserName", userName);

			
			return "mainpage";
			
		} else if(n1.getId() != null && n1.isBlocked() == true){
			
			request.setAttribute("UserName", userName);
			return "redirect:/blocked";
			
			
		} else {
			
			if(userId.equals("115712049459208")) {
				mv.addObject("UserName", userName);
				request.setAttribute("UserName", userName);
				return "redirect:/adminpage";	
				
			} else {
				
				
				mv.addObject("UserName", userName);
				mv.addObject("gameDetails", gameDetails);
				request.setAttribute("UserName", userName);
				return "mainpage";	
			}	
			
			
		}
		}
		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@RequestMapping("/delete_user")
	public String deleteUser(@RequestParam Long id, HttpServletRequest request) {
		userrepo.deleteById(id);
		request.setAttribute("users", userrepo.findAll());
		return "redirect:/adminpage";
	}
	
	
	@RequestMapping("/block_user")
	public String blockUser(@RequestParam Long id, HttpServletRequest request) {
		
			
			Optional<User> n = null;	
			n = userrepo.findById(id);
			User n1 = n.get();
			n1.setBlocked(true);
			n1.setId(id);
			n1.setName(n1.getName());
			userrepo.save(n1);
		    request.setAttribute("users", userrepo.findAll());
		    return "redirect:/adminpage";
	}
	
	@RequestMapping("/unblock_user")
	public String unblockUser(@RequestParam Long id, HttpServletRequest request) {
		
		   Optional<User> n = null;	
		   n = userrepo.findById(id);
		   User n1 = n.get();
		   n1.setBlocked(false);
		   n1.setId(id);
		   n1.setName(n1.getName());
		   userrepo.save(n1);
		   request.setAttribute("users", userrepo.findAll());
		   return "redirect:/adminpage";
	}	
	

	
	
	
///////////////////Score Board//////////////////////////////////
	
	//Using objectMapper
		@RequestMapping("/ScoreBoard")
		public ModelAndView ScoreBoard(HttpSession session) {
			
			ModelAndView scoreboard = new ModelAndView();
			ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
			
			
			  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyddMM");
			  LocalDateTime now = LocalDateTime.now();
			  System.out.println("Checking Data"+dtf.format(now));
			  String x = dtf.format(now);
			  System.out.println("Checking x"+x);
			  System.out.println();
			
			
			String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/scoreboard.json?fordate=20181112";
			
			String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
	       
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Basic "+encoding);
			HttpEntity<String> request = new HttpEntity<String>(headers);

			
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			String str = response.getBody(); 
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode root = mapper.readTree(str);
				System.out.println(str);
				//JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
		        System.out.println(root.get("scoreboard").get("lastUpdatedOn").asText());
		        System.out.println(root.get("scoreboard").get("gameScore").getNodeType());
		        JsonNode gameScore = root.get("scoreboard").get("gameScore");
		        
		        if(gameScore.isArray()) {
		        	
		        	gameScore.forEach(gamescr -> {
		        		JsonNode game = gamescr.get("game");
		        		HashMap<String,String> gameDetail = new HashMap<String, String>();
		        	
		        		gameDetail.put("homeTeam", game.get("homeTeam").get("Name").asText());
		        		gameDetail.put("awayTeam", game.get("awayTeam").get("Name").asText());
		        		gameDetail.put("homeScore",gamescr.get("homeScore").asText());
		        		gameDetail.put("awayScore",gamescr.get("awayScore").asText());
		        		//gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
		        		gameDetails.add(gameDetail);
		        		
		        	});
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
			scoreboard.addObject("gameDetails", gameDetails);
			scoreboard.setViewName("ScoreBoard");
			return scoreboard;
		}	
	
		 @GetMapping("/selectfavouriteteams")
		 public ModelAndView selectfavteams(HttpSession session) {
							
				ModelAndView favteams = new ModelAndView();
				
				//Endpoint to call
				String url ="https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
				//Encode Username and Password
		        String encoding = Base64.getEncoder().encodeToString("e1591372-4f04-4797-b24f-bd817b:123456".getBytes());
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("Authorization", "Basic "+encoding);
				HttpEntity<String> request = new HttpEntity<String>(headers);
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<NBATeamStanding> response = restTemplate.exchange(url, HttpMethod.GET, request, NBATeamStanding.class);
				NBATeamStanding ts = response.getBody(); 
		        System.out.println(ts.toString());
				//Send the object to view
		        favteams.addObject("teamStandingEntries", ts.getOverallteamstandings().getTeamstandingsentries());
		        favteams.setViewName("selectfavouriteteams");
				//return showTeams;
				return favteams;
			
			}		
		
@PostMapping("/selectfavouriteteams")		
public String selectfavteamspost(@RequestParam String selteams, HttpServletRequest request) {
	
	
	   Selectfaveteams teams = new Selectfaveteams();
	    
	   HttpSession session = request.getSession();
	   
	    Object userid = session.getAttribute("userId");
	    
	    String user = (String) userid; 
	     teams.setId(Long.parseLong(user));	
	     teams.setTeamslist(selteams);	
		 teamsrepo.save(teams);
		
	return "redirect:/mainpage";

}			

}