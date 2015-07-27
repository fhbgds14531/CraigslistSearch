package csearch.prog.fhbgds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CSearch {
		public static void main(String[] args){
			new CSearch(new Scanner(System.in));
		}
		
		public CSearch(Scanner in){
			String initialInput = getInput("Enter the zipcode of the city you wish to search, or \"STOP\" if you wish to stop.", in);
			this.handleInput(in, initialInput);
			in.close();
		}
		
		/**
		 * Handles everything.
		 * @param in {@link Scanner}, only used if something goes wrong and the user should input a new zip
		 * @param initialInput {@link String} the user's input
		 */
		public void handleInput(Scanner in, String initialInput){
			initialInput = initialInput.trim();
			if(initialInput.toUpperCase().contentEquals("STOP")) return;
			
			if(checkValid(initialInput)){
				
				//Get the city, state and country from the zipcode. (they are in that order)
				String[] loc = Net.getLocationFromZipcode(initialInput);
				if(loc == null){
					throw new RuntimeException("Error retrieving location from the web. Either you entered an invalid zipcode, or you can't connect to the internet.");
				}else{
					URIBuilder uriB = new URIBuilder();
					
					//encode city name into search uri. I'm probably implementing this wrong, but it works.
					uriB.setPath(String.format("http://%s.craigslist.org/search/sof?", loc[0].replace(" ", "")));
					URI uri = null;
					try {
						uri = uriB.build();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
					Document doc = null;
					try {
						
						// get the craigslist page to scan through
						doc = Jsoup.connect(uri.toURL().toString()).get();
					}catch(UnknownHostException e){
						System.err.println(String.format("ERROR: Could not retrieve listings for %s, %s.\nThis could be caused by the craigslist page for your area being based out of another city (e.g. the listings for Beverly Hills, CA [90210] are on the Los Angeles, CA page).\nTry another zipcode please.", loc[0], loc[1]));
						try { 	Thread.sleep(10l);	} catch (InterruptedException e1) {e1.printStackTrace();} //Without this the "> " for the input method shows up in the middle of the exception message.
						handleInput(in, getInput("", in));
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if(doc == null) return;
					//find the element containing the total number of listings
					Elements e = doc.body().getElementsByClass("totalcount");
					if(!e.isEmpty()){
						//extract the amount of listings into an int
						int numOfListings = Integer.valueOf(e.get(0).text()); 
						System.out.format("There are %s software jobs listed in %s, %s right now. ", numOfListings, loc[0], loc[1]);
						Demand level = Demand.getDemandLevel(numOfListings);
						System.out.format("Demand is %s\n", level == Demand.HUGE ? "huge; Start looking for free training!" : level + ".");
					}
					
				}
			}else{
				handleInput(in, getInput("", in));
			}
		}
		
		/**check that the input is 5 characters long and print a message if it's not
		 * 
		 * @param zipString {@link String} to check
		 * @return whether or not the {@link String} is exactly 5 characters long
		 */
		public boolean checkValid(String zipString){
			if(zipString.length() != 5){
				System.err.println("Please enter a valid Zipcode. (5 digits)");
				return false;
			}
			return true;
		}
		
		/**
		 * Convenience method, also adds a "> " to the input line because it looks better
		 * @param prompt This method prints whatever is in this field before asking for input, unless it is blank.
		 * @param in {@link Scanner} to read from.
		 * @return the user's input.
		 */
		public String getInput(String prompt, Scanner in){
			if(!prompt.isEmpty()) System.out.println(prompt);
			System.out.format("> ");
			return in.nextLine();
		}
}
