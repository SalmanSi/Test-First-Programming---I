package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T13:00:00Z");
    private static final Instant d5 = Instant.parse("2016-02-17T14:00:00Z");
    
    /* Testing Strategy
     * 
     * guessFollowsGraph():
     * Partition on number of tweets:
     * - empty list of tweets
     * - single tweet
     * - multiple tweets
     * 
     * Partition on mentions in tweets:
     * - no mentions
     * - single mention
     * - multiple mentions
     * - same user mentioned multiple times
     * 
     * Partition on authors:
     * - different authors
     * - same author multiple tweets
     * - case sensitivity in usernames
     * 
     * influencers():
     * Partition on graph size:
     * - empty graph
     * - single user
     * - multiple users
     * 
     * Partition on follower count:
     * - no followers
     * - one follower
     * - multiple followers
     * - equal number of followers
     */

    // Sample tweets for testing
    private static final Tweet tweet1 = new Tweet(1, "user1", "Just a normal tweet", d1);
    private static final Tweet tweet2 = new Tweet(2, "user2", "@user3 Hello there!", d2);
    private static final Tweet tweet3 = new Tweet(3, "user1", "@user2 @user3 Hi both", d3);
    private static final Tweet tweet4 = new Tweet(4, "USER4", "@User2 case insensitive", d4);
    private static final Tweet tweet5 = new Tweet(5, "user5", "@user2 @user2 @user3 multiple mentions", d5);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // Tests for guessFollowsGraph()
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("Expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions() {
        List<Tweet> tweets = Arrays.asList(tweet1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("Expected empty set for user with no mentions", 
            followsGraph.containsKey("user1") && followsGraph.get("user1").isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        List<Tweet> tweets = Arrays.asList(tweet2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("Expected single follow relationship", 
            followsGraph.get("user2").contains("user3"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        List<Tweet> tweets = Arrays.asList(tweet3);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> expected = new HashSet<>(Arrays.asList("user2", "user3"));
        assertEquals("Expected two follows relationships", expected, followsGraph.get("user1"));
    }

    @Test
    public void testGuessFollowsGraphCaseInsensitive() {
        List<Tweet> tweets = Arrays.asList(tweet4);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("Expected case-insensitive follows", 
            followsGraph.get("user4").contains("user2"));
    }

    @Test
    public void testGuessFollowsGraphDuplicateMentions() {
        List<Tweet> tweets = Arrays.asList(tweet5);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> expected = new HashSet<>(Arrays.asList("user2", "user3"));
        assertEquals("Expected no duplicate follows", expected, followsGraph.get("user5"));
    }

    // Tests for influencers()

    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("Expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("Expected single user", Arrays.asList("user1"), influencers);
    }

    @Test
    public void testInfluencersSingleFollower() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2")));
        followsGraph.put("user2", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("Expected user2 as top influencer", "user2", influencers.get(0));
    }

    @Test
    public void testInfluencersMultipleFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2", "user3")));
        followsGraph.put("user2", new HashSet<>(Arrays.asList("user3")));
        followsGraph.put("user3", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("Expected user3 as top influencer", "user3", influencers.get(0));
    }


}