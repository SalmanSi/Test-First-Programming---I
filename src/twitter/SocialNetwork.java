package twitter;

import java.util.*;

public class SocialNetwork {
    
    /**
     * Guess who might follow whom, from evidence found in tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        
        // Process each tweet individually to get mentions
        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            // Initialize empty set for author if not present
            followsGraph.putIfAbsent(author, new HashSet<>());
            
            // Get mentions from single tweet using Extract.getMentionedUsers
            Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweet));
            
            // Add mentions to author's following set
            for (String mention : mentions) {
                String mentionLower = mention.toLowerCase();
                if (!mentionLower.equals(author)) {  // Users can't follow themselves
                    followsGraph.get(author).add(mentionLower);
                }
                // Ensure mentioned user exists in graph
                followsGraph.putIfAbsent(mentionLower, new HashSet<>());
            }
        }
        
        return followsGraph;
    }

    /**
     * Find the people who have the greatest influence.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        // Count followers for each user
        Map<String, Integer> followerCounts = new HashMap<>();
        
        // Initialize counts
        for (String user : followsGraph.keySet()) {
            followerCounts.put(user, 0);
        }
        
        // Count followers for each user
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            for (String followedUser : entry.getValue()) {
                followerCounts.merge(followedUser, 1, Integer::sum);
            }
        }
        
        // Create and sort list of users by follower count
        List<String> sortedUsers = new ArrayList<>(followsGraph.keySet());
        sortedUsers.sort((a, b) -> {
            int countCompare = followerCounts.getOrDefault(b, 0)
                .compareTo(followerCounts.getOrDefault(a, 0));
            return countCompare != 0 ? countCompare : a.compareTo(b);
        });
        
        return sortedUsers;
    }
}