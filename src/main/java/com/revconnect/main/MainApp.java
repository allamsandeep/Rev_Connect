package com.revconnect.main;

import java.util.List;
import java.util.Scanner;

import com.revconnect.model.*;
import com.revconnect.service.*;

public class MainApp {

    private static final Scanner sc = new Scanner(System.in);

    private static final UserService userService = new UserService();
    private static final ProfileService profileService = new ProfileService();
    private static final PostService postService = new PostService();
    private static final CommentService commentService = new CommentService();
    private static final ConnectionService connectionService = new ConnectionService();
    private static final FollowService followService = new FollowService();
    private static final NotificationService notificationService = new NotificationService(); // ✅ NEW
    private static final BusinessHourService businessHourService =  new BusinessHourService();


    private static User loggedInUser = null;

    public static void main(String[] args) {

        boolean running = true;

        while (running) {
            if (loggedInUser == null) {
                running = authMenu();
            } else {
                dashboardMenu();
            }
        }

        sc.close();
        System.out.println("👋 Thank you for using RevConnect");
    }

    // ================= AUTH MENU =================
    private static boolean authMenu() {

        System.out.println("\n=== AUTH MENU ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Forgot Password");
        System.out.println("4. Exit");
        System.out.print("Choose option: ");

        int choice = readInt();

        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> forgotPassword();
            case 4 -> { return false; }
            default -> System.out.println("❌ Invalid option");
        }
        return true;
    }

    // ================= REGISTER =================
    private static void registerUser() {

        User user = new User();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        // ✅ BASIC VALIDATION
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Email, Username, and Password cannot be empty");
            return;
        }

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        // ✅ USER TYPE VALIDATION
        while (true) {
            System.out.print("User Type (PERSONAL / CREATOR / BUSINESS): ");
            String input = sc.nextLine().trim().toUpperCase();

            try {
                user.setUserType(UserType.valueOf(input));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid user type. Please enter PERSONAL, CREATOR, or BUSINESS only.");
            }
        }

        System.out.print("Security Question: ");
        user.setSecurityQuestion(sc.nextLine().trim());

        System.out.print("Security Answer: ");
        user.setSecurityAnswer(sc.nextLine().trim());

        System.out.println(
                userService.registerUser(user)
                        ? "✅ Registration successful!"
                        : "❌ Registration failed (Username or Email may already exist)"
        );
    }


    // ================= LOGIN =================
    private static void loginUser() {

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        loggedInUser = userService.login(username, password);

        if (loggedInUser != null) {
            System.out.println("✅ Login successful! Welcome " + loggedInUser.getUsername());

            int unread =
                    notificationService.getUnreadCount(loggedInUser.getUserId());

            if (unread > 0) {
                System.out.println("🔔 You have " + unread + " unread notifications");
            }
        } else {
            System.out.println("❌ Invalid credentials");
        }
    }

    // ================= FORGOT PASSWORD =================
    private static void forgotPassword() {
        System.out.print("Username: ");
        String username = sc.nextLine();

        // 1️⃣ Fetch security question
        String question = userService.getSecurityQuestion(username);

        if (question == null) {
            System.out.println("❌ User not found");
            return;
        }

        // 2️⃣ Show security question
        System.out.println("Security Question: " + question);

        // 3️⃣ Ask answer
        System.out.print("Security Answer: ");
        String answer = sc.nextLine();

        // 4️⃣ Ask new password
        System.out.print("New Password: ");
        String newPassword = sc.nextLine();

        System.out.print("Confirm Password: ");
        String confirmPassword = sc.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords do not match");
            return;
        }

        // 5️⃣ Reset password
        boolean reset = userService.resetPassword(username, answer, newPassword);

        if (reset) {
            System.out.println("✅ Password reset successful");
        } else {
            System.out.println("❌ Incorrect security answer");
        }

    }

    // ================= DASHBOARD =================
    private static void dashboardMenu() {

        boolean inDashboard = true;

        while (inDashboard) {

            System.out.println("\n=== DASHBOARD ===");
            System.out.println("Logged in as: " + loggedInUser.getUsername());
            System.out.println("1. Profile Menu");
            System.out.println("2. Post Menu");
            System.out.println("3. Connection Menu");
            System.out.println("4. Follow Menu");
            System.out.println("5. Notification Menu"); // ✅ NEW
            System.out.println("6. Search Users");
            System.out.println("7. View Other User Profile");
            System.out.println("8. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> profileMenu();
                case 2 -> postMenu();
                case 3 -> connectionMenu();
                case 4 -> followMenu();
                case 5 -> notificationMenu(); // ✅ NEW
                case 6 -> searchUsers();
                case 7 -> viewOtherUserProfile();
                case 8 -> {
                    loggedInUser = null;
                    inDashboard = false;
                }
                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= NOTIFICATION MENU =================
    private static void notificationMenu() {

        boolean inNotify = true;

        while (inNotify) {

            System.out.println("\n=== 🔔 NOTIFICATION MENU ===");
            System.out.println("1. View Unread Notifications");
            System.out.println("2. View All Notifications");
            System.out.println("3. Mark All as Read");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {

                case 1 -> {
                    List<String> unread =
                            notificationService.getUnreadNotifications(
                                    loggedInUser.getUserId());

                    if (unread.isEmpty()) {
                        System.out.println("📭 No unread notifications");
                    } else {
                        unread.forEach(System.out::println);
                    }
                }

                case 2 -> {
                    List<String> all =
                            notificationService.getAllNotifications(
                                    loggedInUser.getUserId());

                    if (all.isEmpty()) {
                        System.out.println("📭 No notifications yet");
                    } else {
                        all.forEach(System.out::println);
                    }
                }

                case 3 -> {
                    notificationService.markAllAsRead(
                            loggedInUser.getUserId());
                    System.out.println("✅ All notifications marked as read");
                }

                case 4 -> inNotify = false;
                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= FOLLOW MENU =================
    private static void followMenu() {
        /* unchanged – your existing code */
        boolean inFollow = true;
        while (inFollow) {
            System.out.println("\n=== FOLLOW MENU ===");
            System.out.println("1. Follow User");
            System.out.println("2. Unfollow User");
            System.out.println("3. View My Followers");
            System.out.println("4. View My Following");
            System.out.println("5. Back");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Search username: ");
                    List<User> users = userService.searchUsers(sc.nextLine());
                    users.forEach(u ->
                            System.out.println("User ID: " + u.getUserId()
                                    + " | Username: " + u.getUsername()));
                    System.out.print("Enter User ID to follow: ");
                    System.out.println(
                            followService.followUser(loggedInUser.getUserId(), readInt())
                                    ? "✅ Now following user"
                                    : "❌ Failed");
                }
                case 2 -> {
                    followService.viewFollowing(loggedInUser.getUserId())
                            .forEach(u -> System.out.println(u.getUsername()));
                    System.out.print("Enter User ID to unfollow: ");
                    System.out.println(
                            followService.unfollowUser(loggedInUser.getUserId(), readInt())
                                    ? "🚫 Unfollowed"
                                    : "❌ Failed");
                }
                case 3 -> followService.viewFollowers(loggedInUser.getUserId())
                        .forEach(u ->
                                System.out.println("User ID: " + u.getUserId()
                                        + " | Username: " + u.getUsername()));

                case 4 -> followService.viewFollowing(loggedInUser.getUserId())
                        .forEach(u ->
                                System.out.println("User ID: " + u.getUserId()
                                        + " | Username: " + u.getUsername()));

                case 5 -> inFollow = false;
            }
        }
    }

    // ================= CONNECTION MENU =================
    private static void connectionMenu() {
        /* unchanged – your existing code */
        boolean inConnection = true;
        while (inConnection) {
            System.out.println("\n=== CONNECTION MENU ===");
            System.out.println("1. Send Connection Request");
            System.out.println("2. View Pending Requests");
            System.out.println("3. Accept Request");
            System.out.println("4. Reject Request");
            System.out.println("5. View Connections");
            System.out.println("6. Remove Connection");
            System.out.println("7. Back");


            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {

                case 1 -> {
                    System.out.print("Enter User ID to connect: ");
                    int targetUserId = readInt();

                    boolean sent = connectionService.sendConnectionRequest(
                            loggedInUser.getUserId(),
                            targetUserId
                    );

                    if (sent) {
                        System.out.println("📨 Connection request sent");
                    } else {
                        System.out.println("⚠ Connection already exists or request already sent");
                    }
                }

                case 2 -> connectionService.viewPendingRequests(
                        loggedInUser.getUserId()).forEach(System.out::println);

                case 3 -> {
                    System.out.print("Enter Connection ID: ");
                    System.out.println(
                            connectionService.acceptRequest(readInt())
                                    ? "✅ Accepted"
                                    : "❌ Failed");
                }

                case 4 -> {
                    System.out.print("Enter Connection ID: ");
                    System.out.println(
                            connectionService.rejectRequest(readInt())
                                    ? "❌ Rejected"
                                    : "❌ Failed");
                }

                case 5 -> {   // 👥 VIEW CONNECTIONS (FIXED)
                    System.out.println("\n=== MY CONNECTIONS ===");

                    var connections = connectionService.viewConnections(
                            loggedInUser.getUserId());

                    if (connections.isEmpty()) {
                        System.out.println("⚠ No connections yet");
                    } else {
                        connections.forEach(System.out::println);
                    }
                }

                case 6 -> {   // 🔗 REMOVE CONNECTION (FIXED)
                    System.out.print("Enter connected User ID to remove: ");
                    int otherUserId = readInt();

                    boolean removed = connectionService.removeConnection(
                            loggedInUser.getUserId(),
                            otherUserId
                    );

                    if (removed) {
                        System.out.println("❌ Connection removed successfully");
                    } else {
                        System.out.println("⚠ No active connection found");
                    }
                }

                case 7 -> inConnection = false; // ✅ BACK
            }


        }
    }

    // ================= PROFILE MENU =================
    private static void profileMenu() {
        boolean inProfile = true;

        while (inProfile) {
            System.out.println("1. Create Profile");
            System.out.println("2. View Profile");
            System.out.println("3. Update Profile");
            System.out.println("4. Change Password");
            System.out.println("5. Privacy Settings");
            System.out.println("6. Back");

            if (loggedInUser.getUserType() == UserType.BUSINESS) {
                System.out.println("7. View Business Hours");
                System.out.println("8. Set Business Hours");

            }
            if (loggedInUser.getUserType() == UserType.BUSINESS ||
                    loggedInUser.getUserType() == UserType.CREATOR) {
                System.out.println("9. Set External Links");   // ✅ NEW
            }




            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {

                case 1 -> {
                    Profile p = new Profile();
                    p.setUserId(loggedInUser.getUserId());

                    System.out.print("Full Name: ");
                    p.setFullName(sc.nextLine());

                    System.out.print("Bio: ");
                    p.setBio(sc.nextLine());

                    System.out.print("Location: ");
                    p.setLocation(sc.nextLine());

                    System.out.print("Website: ");
                    p.setWebsite(sc.nextLine());

                    if (loggedInUser.getUserType() == UserType.CREATOR ||
                            loggedInUser.getUserType() == UserType.BUSINESS) {
                        System.out.print("Category / Industry: ");
                        p.setCategory(sc.nextLine());
                    }

                    if (loggedInUser.getUserType() == UserType.BUSINESS) {
                        System.out.print("Contact Info: ");
                        p.setContactInfo(sc.nextLine());
                    }

                    System.out.print("Profile Visibility (PUBLIC / PRIVATE): ");
                    p.setProfileVisibility(sc.nextLine().toUpperCase());

                    System.out.println(
                            profileService.createProfile(p)
                                    ? "✅ Profile created"
                                    : "❌ Failed"
                    );
                }

                case 2 -> System.out.println(
                        profileService.viewProfile(loggedInUser.getUserId())
                );

                case 3 -> {
                    Profile p = new Profile();
                    p.setUserId(loggedInUser.getUserId());

                    System.out.print("New Name: ");
                    p.setFullName(sc.nextLine());

                    System.out.print("New Bio: ");
                    p.setBio(sc.nextLine());

                    System.out.print("New Location: ");
                    p.setLocation(sc.nextLine());

                    System.out.print("New Website: ");
                    p.setWebsite(sc.nextLine());

                    System.out.println(
                            profileService.updateProfile(p)
                                    ? "✅ Profile updated"
                                    : "❌ Failed"
                    );
                }

                case 4 -> {   // 🔐 Change Password
                    System.out.print("Enter current password: ");
                    String currentPassword = sc.nextLine();

                    System.out.print("Enter new password: ");
                    String newPassword = sc.nextLine();

                    System.out.print("Confirm new password: ");
                    String confirmPassword = sc.nextLine();

                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("❌ Passwords do not match");
                        break;
                    }

                    if (newPassword.equals(currentPassword)) {
                        System.out.println("❌ New password cannot be same as current password");
                        break;
                    }

                    boolean changed = userService.changePassword(
                            loggedInUser.getUserId(),
                            currentPassword,
                            newPassword
                    );

                    System.out.println(
                            changed
                                    ? "✅ Password changed successfully"
                                    : "❌ Current password is incorrect"
                    );
                }

                case 5 -> {   // 🔐 Privacy Settings
                    System.out.print("Set profile visibility (PUBLIC / PRIVATE): ");
                    String visibility = sc.nextLine().toUpperCase();

                    if (!visibility.equals("PUBLIC") && !visibility.equals("PRIVATE")) {
                        System.out.println("❌ Invalid option");
                        break;
                    }

                    boolean updated = profileService.updatePrivacy(
                            loggedInUser.getUserId(),
                            visibility
                    );

                    System.out.println(
                            updated
                                    ? "✅ Profile visibility updated to " + visibility
                                    : "❌ Failed to update privacy"
                    );
                }

                case 6 -> inProfile = false;

                // ✅ NEW: VIEW BUSINESS HOURS
                case 7 -> {
                    if (loggedInUser.getUserType() != UserType.BUSINESS) {
                        System.out.println("❌ Invalid option");
                        break;
                    }

                    String hours =
                            profileService.viewBusinessHours(
                                    loggedInUser.getUserId()
                            );

                    if (hours == null || hours.isBlank()) {
                        System.out.println("📭 Business hours not set");
                    } else {
                        System.out.println("\n🕒 BUSINESS HOURS");
                        System.out.println(hours);
                    }
                }

                case 8 -> {
                    if (loggedInUser.getUserType() != UserType.BUSINESS) {
                        System.out.println("❌ Only business accounts can set business hours");
                        break;
                    }

                    System.out.println("Enter business hours (example format):");
                    System.out.println("MON-FRI: 09:00 - 18:00");
                    System.out.println("SAT: 10:00 - 14:00");
                    System.out.println("SUN: Closed");
                    System.out.print("Business Hours: ");

                    String hours = sc.nextLine();

                    boolean updated = profileService.updateBusinessHours(
                            loggedInUser.getUserId(),
                            hours
                    );

                    System.out.println(updated
                            ? "✅ Business hours updated successfully"
                            : "❌ Failed to update business hours");
                }

                case 9 -> {
                    if (loggedInUser.getUserType() != UserType.BUSINESS &&
                            loggedInUser.getUserType() != UserType.CREATOR) {
                        System.out.println("❌ Only business/creator accounts can set external links");
                        break;
                    }

                    System.out.println("Enter external links (type END to finish):");
                    System.out.println("Example:");
                    System.out.println("Website: https://example.com");
                    System.out.println("Instagram: https://instagram.com/brand");
                    System.out.println("LinkedIn: https://linkedin.com/company/brand");
                    System.out.println("END");

                    StringBuilder links = new StringBuilder();
                    String line;

                    while (!(line = sc.nextLine()).equalsIgnoreCase("END")) {
                        links.append(line).append("\n");
                    }

                    boolean updated = profileService.updateExternalLinks(
                            loggedInUser.getUserId(),
                            links.toString()
                    );

                    System.out.println(updated
                            ? "✅ External links updated successfully"
                            : "❌ Failed to update external links");
                }


                default -> System.out.println("❌ Invalid option. Please choose a valid menu option.");
            }

        }
    }

    // ================= POST MENU =================
    private static void postMenu() {

        boolean inPost = true;

        while (inPost) {

            System.out.println("\n=== POST MENU ===");
            System.out.println("1. Create Post");
            System.out.println("2. View Global Feed");
            System.out.println("3. View Personalized Feed");
            System.out.println("4. View My Posts Only");
            System.out.println("5. Like / Unlike Post");
            System.out.println("6. Add Comment");
            System.out.println("7. View Comments");
            System.out.println("8. Delete Comment");
            System.out.println("9. Edit Post");
            System.out.println("10. Delete Post");
            System.out.println("11. Search by Hashtag");
            System.out.println("12. Trending Hashtags");
            System.out.println("13. Share / Repost");
            System.out.println("14. Filter Posts by User Type");
            System.out.println("15. Filter Shared Posts");

            // 🔥 BUSINESS OPTIONS
            if (loggedInUser.getUserType() == UserType.BUSINESS ||
                    loggedInUser.getUserType() == UserType.CREATOR) {
                System.out.println("16. Pin a Post");
                System.out.println("17. Unpin a Post");
                System.out.println("18. View Post Analytics");
                System.out.println("19. Back");
            } else {
                System.out.println("16. Back");
            }
            System.out.print("Choose option: ");


            int choice = readInt();

            switch (choice) {

// ================= CREATE POST =================
                case 1 -> {
                    Post post = new Post();
                    post.setUserId(loggedInUser.getUserId());

                    System.out.print("Post Content: ");
                    post.setContent(sc.nextLine());

                    // 🔥 BUSINESS / CREATOR FEATURE
                    if (loggedInUser.getUserType() == UserType.BUSINESS ||
                            loggedInUser.getUserType() == UserType.CREATOR) {

                        System.out.print("Post Type (NORMAL / PROMOTIONAL): ");
                        post.setPostType(sc.nextLine().toUpperCase());
                    } else {
                        post.setPostType("NORMAL");
                    }

                    // ✅ IMPORTANT: DEFAULT PINNED VALUE
                    post.setPinned(false);

                    System.out.println(
                            postService.createPost(post)
                                    ? "✅ Post created"
                                    : "❌ Post failed"
                    );
                }


                // ================= GLOBAL FEED =================
                case 2 -> {
                    System.out.println("\n🌍 GLOBAL FEED");
                    List<Post> posts = postService.viewAllPosts();

                    if (posts.isEmpty()) {
                        System.out.println("No posts available");
                    }

                    posts.forEach(p -> {
                        System.out.println("\nPost ID : " + p.getPostId());
                        System.out.println("User    : " + p.getUsername());
                        System.out.println("Post    : " + p.getContent());
                        System.out.println("Likes   : " + postService.getLikeCount(p.getPostId()));
                        System.out.println("Date    : " + p.getCreatedAt());
                    });
                }

                // ================= PERSONALIZED FEED =================
                case 3 -> {
                    System.out.println("\n🎯 PERSONALIZED FEED");
                    List<Post> posts =
                            postService.viewPersonalizedFeed(loggedInUser.getUserId());

                    if (posts.isEmpty()) {
                        System.out.println("No posts from your connections/following yet");
                    }

                    posts.forEach(p -> {
                        System.out.println("\nPost ID : " + p.getPostId());
                        System.out.println("User    : " + p.getUsername());
                        System.out.println("Post    : " + p.getContent());
                        System.out.println("Likes   : " + postService.getLikeCount(p.getPostId()));
                        System.out.println("Date    : " + p.getCreatedAt());
                    });
                }

                // ================= MY POSTS ONLY =================
                case 4 -> {
                    System.out.println("\n👤 MY POSTS");
                    List<Post> posts =
                            postService.viewMyPosts(loggedInUser.getUserId());

                    if (posts.isEmpty()) {
                        System.out.println("You haven’t posted anything yet");
                    }

                    posts.forEach(p -> {
                        System.out.println("\nPost ID : " + p.getPostId());
                        System.out.println("Post    : " + p.getContent());
                        System.out.println("Likes   : " + postService.getLikeCount(p.getPostId()));
                        System.out.println("Date    : " + p.getCreatedAt());
                    });
                }

                // ================= LIKE / UNLIKE =================
                case 5 -> {
                    System.out.print("Enter Post ID: ");
                    int postId = readInt();

                    boolean liked = postService.toggleLike(
                            postId,
                            loggedInUser.getUserId()
                    );

                    if (liked) {
                        System.out.println("❤️ Post liked");
                    } else {
                        System.out.println("💔 Post unliked");
                    }
                }


                // ================= ADD COMMENT =================
                case 6 -> {
                    System.out.print("Enter Post ID: ");
                    int postId = readInt();

                    System.out.print("Enter comment: ");
                    String text = sc.nextLine();

                    System.out.println(
                            commentService.addComment(postId, loggedInUser.getUserId(), text)
                                    ? "💬 Comment added"
                                    : "❌ Failed"
                    );
                }

                // ================= VIEW COMMENTS =================
                case 7 -> {
                    System.out.print("Enter Post ID: ");
                    int postId = readInt();

                    List<String> comments =
                            commentService.viewComments(postId);

                    if (comments.isEmpty()) {
                        System.out.println("No comments yet");
                    } else {
                        comments.forEach(System.out::println);
                    }
                }

                // ================= DELETE COMMENT =================
                case 8 -> {
                    System.out.print("Enter Comment ID: ");
                    int commentId = readInt();

                    System.out.println(
                            commentService.deleteComment(commentId, loggedInUser.getUserId())
                                    ? "🗑️ Comment deleted"
                                    : "❌ Cannot delete"
                    );
                }

                // ================= EDIT POST =================
                case 9 -> {
                    System.out.print("Enter Post ID: ");
                    int postId = readInt();

                    System.out.print("New content: ");
                    String content = sc.nextLine();

                    postService.editPost(postId, loggedInUser.getUserId(), content);
                    System.out.println("✅ Post updated");
                }

                // ================= DELETE POST =================
                case 10 -> {
                    System.out.print("Enter Post ID: ");
                    int postId = readInt();

                    postService.deletePost(postId, loggedInUser.getUserId());
                    System.out.println("🗑️ Post deleted");
                }

                // ================= SEARCH BY HASHTAG =================
                case 11 -> {
                    System.out.print("Hashtag: ");
                    postService.searchPostsByHashtag(sc.nextLine())
                            .forEach(p -> System.out.println(p.getContent()));
                }

                // ================= TRENDING HASHTAGS =================
                case 12 -> {
                    System.out.println("\n🔥 TRENDING HASHTAGS");
                    postService.getTrendingHashtags(5)
                            .forEach(t -> System.out.println("#" + t));
                }

                // ================= SHARE / REPOST =================
                case 13 -> {
                    System.out.print("Enter Post ID to share: ");
                    int postId = readInt();

                    System.out.print("Optional comment (press Enter to skip): ");
                    String comment = sc.nextLine();

                    boolean shared = postService.sharePost(
                            postId,
                            loggedInUser.getUserId(),
                            comment
                    );

                    System.out.println(shared
                            ? "🔁 Post shared successfully"
                            : "❌ Failed to share post");
                }
                // ================= FILTER BY USER TYPE =================
                case 14 -> {
                    System.out.print("Enter User Type (PERSONAL / CREATOR / BUSINESS): ");
                    String userType = sc.nextLine().toUpperCase();

                    List<Post> posts = postService.filterByUserType(userType);

                    if (posts.isEmpty()) {
                        System.out.println("No posts found for this user type");
                    } else {
                        posts.forEach(p -> {
                            System.out.println("\nPost ID : " + p.getPostId());
                            System.out.println("User    : " + p.getUsername());
                            System.out.println("Post    : " + p.getContent());
                            System.out.println("Date    : " + p.getCreatedAt());
                        });
                    }
                }
                // ================= FILTER SHARED POSTS =================
                case 15 -> {
                    List<Post> posts = postService.filterSharedPosts();

                    if (posts.isEmpty()) {
                        System.out.println("No shared posts found");
                    } else {
                        posts.forEach(p -> {
                            System.out.println("\nPost ID : " + p.getPostId());
                            System.out.println("User    : " + p.getUsername());
                            System.out.println("Post    : " + p.getContent());
                            System.out.println("Date    : " + p.getCreatedAt());
                        });
                    }
                }

// ================= PIN POST =================
                case 16 -> {
                    if (loggedInUser.getUserType() == UserType.BUSINESS ||
                            loggedInUser.getUserType() == UserType.CREATOR) {

                        System.out.print("Enter Post ID to pin: ");
                        int postId = readInt();

                        System.out.println(
                                postService.pinPost(postId, loggedInUser.getUserId())
                                        ? "📌 Post pinned successfully"
                                        : "❌ Failed to pin post"
                        );
                    } else {
                        inPost = false;
                    }
                }

                // ================= UNPIN POST =================
                case 17 -> {
                    if (loggedInUser.getUserType() == UserType.BUSINESS ||
                            loggedInUser.getUserType() == UserType.CREATOR) {

                        System.out.print("Enter Post ID to unpin: ");
                        int postId = readInt();

                        System.out.println(
                                postService.unpinPost(postId, loggedInUser.getUserId())
                                        ? "📌 Post unpinned successfully"
                                        : "❌ Failed to unpin post"
                        );
                    } else {
                        System.out.println("❌ Invalid option");
                    }
                }


                // ================= POST ANALYTICS =================
                case 18 -> {
                    if (loggedInUser.getUserType() == UserType.BUSINESS ||
                            loggedInUser.getUserType() == UserType.CREATOR) {

                        System.out.println("\n📊 POST ANALYTICS");

                        postService.getPostAnalytics(loggedInUser.getUserId())
                                .forEach(a -> System.out.println(
                                        "Post ID: " + a.getPostId() +
                                                " | Likes: " + a.getLikes() +
                                                " | Comments: " + a.getComments() +
                                                " | Shares: " + a.getShares()
                                ));
                    } else {
                        System.out.println("❌ Invalid option");
                    }
                }


                // ================= BACK =================
                case 19 -> inPost = false;

                default -> System.out.println("❌ Invalid option");
            }
        }
    }


    // ================= SEARCH USERS =================
    private static void searchUsers() {
        System.out.print("Keyword: ");
        userService.searchUsers(sc.nextLine())
                .forEach(u ->
                        System.out.println("User ID: " + u.getUserId()
                                + " | Username: " + u.getUsername()));
    }

// ================= VIEW OTHER PROFILE =================
private static void viewOtherUserProfile() {

    System.out.print("Username: ");
    String username = sc.nextLine();

    var users = userService.searchUsers(username);

    if (users.isEmpty()) {
        System.out.println("❌ User not found");
        return;
    }

    User targetUser = users.get(0);
    int targetUserId = targetUser.getUserId();

    // ✅ View own profile
    if (targetUserId == loggedInUser.getUserId()) {
        System.out.println(profileService.viewProfile(targetUserId));
        return;
    }

    // ✅ Fetch Profile OBJECT (for privacy check)
    Profile profile = profileService.getProfileByUserId(targetUserId);

    if (profile == null) {
        System.out.println("❌ Profile not found");
        return;
    }

    // 🔐 PRIVACY CHECK
    if ("PRIVATE".equalsIgnoreCase(profile.getProfileVisibility())) {

        boolean connected = connectionService.areConnected(
                loggedInUser.getUserId(),
                targetUserId
        );

        if (!connected) {
            System.out.println("🔒 This profile is private.");
            System.out.println("Send a connection request to view details.");
            return;
        }
    }

    // ✅ Allowed → SHOW PROFILE STRING
    System.out.println(profileService.viewProfile(targetUserId));
}



    // ================= SAFE INT =================
    private static int readInt() {
        while (!sc.hasNextInt()) sc.nextLine();
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }
}
