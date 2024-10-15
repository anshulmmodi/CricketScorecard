import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

class Player {
    String playerName;
    int runsScored;
    int wicketsTaken;

    public Player(String playerName) {
        this.playerName = playerName;
        runsScored = 0;
        wicketsTaken = 0;
    }

    public String getName() {
        return playerName;
    }

    public int getWicketsTaken() {
        return wicketsTaken;
    }

    public int getRunsScored() {
        return runsScored;
    }

    // Method to add runs scored by the player
    public void addRuns(int runs) {
        runsScored += runs;
    }

    public void addWickets() {
        wicketsTaken++;
    }
}

// Class representing a cricket match
class Match {
    private String team1Name;
    private String team2Name;
    private ArrayList<Player> team1Players;
    private ArrayList<Player> team2Players;
    private int overs;
    private int target;
    BufferedWriter writer;
    BufferedWriter writer1;
    BufferedWriter writer2;
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    String write1 = String.format("%20s %20s", "Player Name", "Player Runs ");
    String write2 = String.format("%20s %20s ", "Player Name", "Player Wickets");
    public static String YELLOW = "\u001B[33m";
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";
    public static String BLUE = "\u001B[34m";
    public static String PURPLE = "\u001B[3m";
    public static String CYAN = "\u001B[36m";
    public static String RESET = "\u001B[0m";

    // Constructor to initialize player with a name and zero runs
    public Match(String team1, String team2) {
        try {
            Scanner sc = new Scanner(System.in);
            this.team1Name = team1;
            this.team2Name = team2;
            String prompt = YELLOW + "Set number of overs" + RESET;
            overs = getIntInput(sc, prompt);
            this.target = 0;
            this.team1Players = new ArrayList<>();
            this.team2Players = new ArrayList<>();
            writer = new BufferedWriter(new FileWriter("Match_Scoreboard.txt"));
            writer1 = new BufferedWriter(new FileWriter("Team1 Players.txt"));
            writer2 = new BufferedWriter(new FileWriter("Team2 Players.txt"));
            

            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);

            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/cricket", "root", "root");
        } catch (IOException e) {
            System.out.println(RED + "File Error" + e.getMessage());
        } catch (SQLException e) {
            System.out.println(RED + "SQL Error" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(RED + "Class Error" + e.getMessage());
        } catch (Exception e) {
            System.out.println(RED + "Error" + e.getMessage());
        }
    }

    // Method to get correct integer input from the user
    public static int getIntInput(Scanner sc, String prompt) {
        int input = 0;
        while (true) {
            try {
                System.out.println(prompt);
                input = sc.nextInt();
                sc.nextLine();
                if (input >= 1) {
                    break;
                }
                // Exit the loop if the input is valid
            } catch (InputMismatchException e) {
                System.err.println(RED + "Invalid input. Please enter a valid number." + RESET);
                sc.nextLine(); // Consume the invalid input
            }
        }
        return input;
    }

    // getter methods
    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public int getOvers() {
        return overs;
    }

    public int getTarget() {
        return target;
    }

    public ArrayList<Player> getTeam1Players() {
        return team1Players;
    }

    public ArrayList<Player> getTeam2Players() {
        return team2Players;
    }

    // method to add Player details
    void addPlayers(int num_players) {
        try {
            String name;

            System.out.println();
            System.out.println(GREEN + "Team 1 Player Details" + GREEN);
            writer.write("Team 1 Player Details \n");
            String sql1 = "INSERT INTO team1_players1(pname,pid) Values(?,?)";
            String sql2 = "INSERT INTO team2_players1(pname,pid) Values(?,?)";
            PreparedStatement pst;
            for (int i = 0; i < num_players; i++) {
                System.out.println(CYAN + "Enter player name " + (i + 1) + " of Team 1" + RESET);
                name = sc.nextLine();
                Player p1 = new Player(name);
                team1Players.add(p1);
                writer.write(name);
                writer.newLine();
                pst = con.prepareStatement(sql1);
                pst.setInt(2, i + 1);
                pst.setString(1, name);
                pst.executeUpdate();
            }
            System.out.println();
            System.out.println(GREEN + "Team 2 Player Details" + RESET);
            writer.write("Team 2 Player Details \n");
            for (int i = 0; i < num_players; i++) {
                System.out.println(CYAN + "Enter player name " + (i + 1) + " of Team 2" + RESET);
                name = sc.nextLine();
                Player p2 = new Player(name);
                team2Players.add(p2);
                writer.write(name);
                writer.newLine();
                pst = con.prepareStatement(sql2);
                pst.setInt(2, i + 1);
                pst.setString(1, name);
                pst.executeUpdate();
            }
        } catch (IOException e) {
            System.out.println(RED + "File Error" + e.getMessage());
        } catch (SQLException e) {
            System.out.println(RED + "SQL Error" + e.getMessage());
        } catch (Exception e) {
            System.out.println(RED + "Error" + e.getMessage());
        }
    }

    // method to perform toss for a match using random function
    int toss() {
        try {
            System.out.println(YELLOW + getTeam1Name() + " Captain is spinning the toss" + RESET);
            writer.write("Team " + team1Name + " Captain is spinning the toss \n");
            System.out.println();
            Random random = new Random();
            int toss = random.nextInt(2);
            if (toss == 0) {
                System.out.println(YELLOW + team1Name + " chose to BAT FIRST" + RESET);
                writer.write("Team " + team1Name + " chose to BAT FIRST");
                writer.newLine();
                return 1;
            } else if (toss == 1) {
                System.out.println(YELLOW + team2Name + " chose to BAT FIRST" + RESET);
                writer.write("Team " + team2Name + " chose to BAT FIRST");
                writer.newLine();
                return 2;
            } else {
                return 0;
            }
        } catch (IOException e) {
            System.out.println("File Error " + e.getMessage());
            return -1;
        }
    }

    static Player setBowler(ArrayList<Player> currentOppositionTeam) {
        System.out.println();
        System.out.println(YELLOW + "Select a bowler:");
        System.out.println("Enter bowler no" + RESET);
        for (int i = 0; i < currentOppositionTeam.size(); i++) {
            System.out.println((i + 1) + ". " + currentOppositionTeam.get(i).getName());
        }

        int bowlerChoice;
        Player selectedBowler = null;

        while (true) {
            try {
                bowlerChoice = Integer.parseInt(sc.nextLine());
                if (bowlerChoice >= 1 && bowlerChoice <= currentOppositionTeam.size()) {
                    selectedBowler = currentOppositionTeam.get(bowlerChoice - 1);
                    break; // Exit the loop if the choice is valid
                } else {
                    System.out.println(RED + "Invalid choice. Please select a valid bowler." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }

        System.out.println("You selected " + selectedBowler.getName() + " as the bowler.");
        return selectedBowler;
    }

    // Method for the first innings of the match
    void innings1(int team) {
        try {
            int ballsRemaining = 6 * getOvers();
            int currentOver = 0;
            int currentBall = 0;
            int score = 0;
            int wickets = 0;
            String teamTableName;
            String oppTeamTableName;
            ArrayList<Player> currentTeamList;
            ArrayList<Player> oppositionTeamList;
            ArrayDeque<Player> currentTeam;
            if (team == 1) {
                currentTeam = new ArrayDeque<>(team1Players);
                teamTableName = "team1_players1";
                oppTeamTableName = "team2_players1";
                currentTeamList = new ArrayList<>(team1Players);
                oppositionTeamList = new ArrayList<>(team2Players);
            } else {
                currentTeam = new ArrayDeque<>(team2Players);
                teamTableName = "team2_players1";
                oppTeamTableName = "team1_players1";
                currentTeamList = new ArrayList<>(team2Players);
                oppositionTeamList = new ArrayList<>(team1Players);
            }

            Player currentBatter1 = currentTeam.pop();
            Player currentBatter2 = currentTeam.pop();
            Player currentStriker = currentBatter1;
            Player currentBowler = null;

            while (ballsRemaining != 0 && wickets != team1Players.size() - 1) {
                if (currentBall == 6 || currentBall==0) {
                    currentBowler = setBowler(oppositionTeamList);
                }
                System.out.println("\n");
                System.out.println(CYAN + ". for Dot Ball \n" + "1 for 1 Run \n" + "2 for 2 Runs \n" + "3 for 3 Runs\n"
                        + "4 for 4 Runs\n" + "6 for 6 Runs\n" + "W for Wicket\n" + "Wd for WideBall\n"
                        + "Nb for No Ball \n"
                        + RESET);
                System.out.println("Enter Outcome of " + currentOver + "." + (currentBall + 1));
                String outcome = sc.nextLine();
                switch (outcome) {
                    case "1": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score++;
                        currentStriker.addRuns(1);
                        System.out.println("\n");
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 1 run ");
                        writer.newLine();
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out.println(
                                currentOver + "." + currentBall + " " + currentStriker.getName() + " scored 1 run ");
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter2;
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter1;
                        }
                        ballsRemaining--;
                        break;

                    }

                    case ".": {
                        currentBall++;
                        if (currentBall == 6 || currentBall == 0) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                        }
                        System.out.println("DOT BALL");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        ballsRemaining--;
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " DOT BALL");
                        writer.newLine();
                        break;

                    }

                    case "2": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 2;
                        currentStriker.addRuns(2);
                        System.out.println("\n");
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 2 runs ");
                        writer.newLine();
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out.println(
                                currentOver + "." + currentBall + " " + currentStriker.getName() + " scored 2 runs");
                        ballsRemaining--;
                        break;

                    }

                    case "3": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 3;
                        currentStriker.addRuns(3);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 3 runs ");
                        writer.newLine();
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 3 runs ");
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter2;
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter1;
                        }
                        ballsRemaining--;
                        break;

                    }

                    case "4": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 4;
                        currentStriker.addRuns(4);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 4 runs ");
                        writer.newLine();
                        System.out.println(YELLOW + "FOUR" + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 4 runs ");
                        ballsRemaining--;
                        break;

                    }
                    case "6": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 6;
                        currentStriker.addRuns(6);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 6 run ");
                        writer.newLine();
                        System.out.println(YELLOW + "SIXER" + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);

                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 6 runs ");
                        ballsRemaining--;
                        break;

                    }
                    case "W": {
                        currentBall++;
                        wickets++;
                        currentBowler.addWickets();
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                        }
                        System.out.println(currentStriker.getName() + " got out at " + currentStriker.getRunsScored()
                                + " by " + currentBowler.getName());
                        if (wickets == team1Players.size() - 1) {
                            break;
                        }
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter1 = currentTeam.pop();
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter2 = currentTeam.pop();
                        }
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " got out at " + currentStriker.getRunsScored() + " by "
                                + currentBowler.getName());
                        writer.newLine();
                        ballsRemaining--;
                        break;

                    }
                    case "Nb": {
                        score++;
                        System.out.println(RED + "NO BALL " + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " NO BALL ");
                        writer.newLine();
                        break;

                    }
                    case "Wd": {
                        score++;
                        System.out.println(RED + "WIDE BALL " + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " WIDE BALL ");
                        writer.newLine();
                        break;

                    }

                    default:
                        System.out.println(RED + "Enter valid outcome " + RESET);
                }

            }
            System.out.println("\n \n");
            if (ballsRemaining == 0) {
                System.out.println(YELLOW + "END OF INNINGS");
                writer.newLine();
                writer.write("END OF INNINGS");
                writer.newLine();
            } else if (wickets == team1Players.size() - 1) {
                System.out.println("TEAM ALL OUT");
                writer.newLine();
                writer.write("TEAM ALL OUT");
                writer.newLine();
            }
            target = score + 1;
            System.out.println("\n");

             // Print the final score of first innings and target for second innings
            if (teamTableName == "team1_players1") {
                System.out.println("TEAM " + getTeam1Name() + " scored Runs " + score + "\n");
                System.out.println("TARGET FOR TEAM " + getTeam2Name() + " " + target + RESET);
                writer.newLine();
                writer.write("TEAM " + getTeam1Name() + " scored Runs " + score + "\n");
                writer.write("TARGET FOR TEAM " + getTeam2Name() + " " + target);
                writer.newLine();
                writer.newLine();
                writer1.write(write1);
                writer1.newLine();
                writer2.write(write2);
                writer2.newLine();
                
            } else if (teamTableName == "team2_players1") {
                System.out.println("TEAM " + getTeam2Name() + " scored Runs " + score + "\n");
                System.out.println("TARGET FOR TEAM " + getTeam1Name() + " " + target + RESET);
                writer.newLine();
                writer.write("TEAM " + getTeam2Name() + " scored Runs " + score + "\n");
                writer.write("TARGET FOR TEAM " + getTeam1Name() + " " + target);
                writer.newLine();
                writer.newLine();
                writer2.write(write1);
                writer2.newLine();
                writer1.write(write2);
                writer1.newLine();
            }
            

            // Iterate through the batting team players to update wicketsTaken in the database and write them to the file
            for (int i = 1; i <= team1Players.size(); i++) {
                String sql = "UPDATE " + teamTableName + " SET runsScored = ? WHERE pid = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, currentTeamList.get(i - 1).getRunsScored());
                pst.setInt(2, i);
                pst.executeUpdate();

                if (teamTableName == "team1_players1") {
                    String write = String.format("%15s %15s", currentTeamList.get(i - 1).getName(),
                            currentTeamList.get(i - 1).getRunsScored());
                    writer1.write(write);
                    writer1.newLine();
                } else if (teamTableName == "team2_players1") {
                    String write = String.format("%15s %15s", currentTeamList.get(i - 1).getName(),
                            currentTeamList.get(i - 1).getRunsScored());
                    writer2.write(write);
                    writer2.newLine();
                }
            }

            // Iterate through the bowling team players to update wicketsTaken in the database and write them to the file
            for (int i = 1; i <= oppositionTeamList.size(); i++) {
                String sql = "UPDATE " + oppTeamTableName + " SET wicketsTaken = ? WHERE pid = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, oppositionTeamList.get(i - 1).getWicketsTaken());
                pst.setInt(2, i);
                pst.executeUpdate();

                // Write bowling player details to the respective file
                String write = String.format("%15s %15s",
                        oppositionTeamList.get(i - 1).getName(),
                        oppositionTeamList.get(i - 1).getWicketsTaken());

                if (oppTeamTableName.equals("team1_players1")) {
                    writer1.write(write);
                    writer1.newLine();
                } else if (oppTeamTableName.equals("team2_players1")) {
                    writer2.write(write);
                    writer2.newLine();
                }
            }

           
        } catch (IOException e) {
            System.out.println("IO Error " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    // Method for the second innings of the match
    void innings2(int team) {
        try {
            int ballsRemaining = 6 * getOvers();
            int currentOver = 0;
            int currentBall = 0;
            int score = 0;
            int wickets = 0;
            String teamTableName;
            String oppTeamTableName;
            ArrayList<Player> currentTeamList;
            ArrayList<Player> oppositionTeamList;
            ArrayDeque<Player> currentTeam;
            if (team == 1) {
                currentTeam = new ArrayDeque<>(team1Players);
                teamTableName = "team1_players1";
                oppTeamTableName = "team2_players1";
                currentTeamList = new ArrayList<>(team1Players);
                oppositionTeamList = new ArrayList<>(team2Players);
            } else {
                currentTeam = new ArrayDeque<>(team2Players);
                teamTableName = "team2_players1";
                oppTeamTableName = "team1_players1";
                currentTeamList = new ArrayList<>(team2Players);
                oppositionTeamList = new ArrayList<>(team1Players);
            }

            Player currentBatter1 = currentTeam.pop();
            Player currentBatter2 = currentTeam.pop();
            Player currentStriker = currentBatter1;
            Player currentBowler = null;

            while (ballsRemaining != 0 && wickets != team1Players.size() - 1 && score < target) {
                if (currentBall == 6 || currentBall==0) {
                    currentBowler = setBowler(oppositionTeamList);
                }
                System.out.println("\n");
                System.out.println(CYAN + ". for Dot Ball \n" + "1 for 1 Run \n" + "2 for 2 Runs \n" + "3 for 3 Runs\n"
                        + "4 for 4 Runs\n" + "6 for 6 Runs\n" + "W for Wicket\n" + "Wd for WideBall\n"
                        + "Nb for No Ball \n"
                        + RESET);
                System.out.println("Enter Outcome of " + currentOver + "." + (currentBall + 1));
                String outcome = sc.nextLine();

                switch (outcome) {
                    case "1": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score++;
                        currentStriker.addRuns(1);
                        System.out.println("\n");
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 1 run ");
                        writer.newLine();
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out.println(
                                currentOver + "." + currentBall + " " + currentStriker.getName() + " scored 1 run ");
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter2;
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter1;
                        }
                        ballsRemaining--;
                        break;

                    }
                    case ".": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                        }
                        System.out.println("DOT BALL");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        ballsRemaining--;
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " DOT BALL");
                        writer.newLine();
                        break;

                    }

                    case "2": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 2;
                        currentStriker.addRuns(2);
                        System.out.println("\n");
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 2 runs ");
                        writer.newLine();
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out.println(
                                currentOver + "." + currentBall + " " + currentStriker.getName() + " scored 2 runs");
                        ballsRemaining--;
                        break;

                    }

                    case "3": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 3;
                        currentStriker.addRuns(3);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 3 runs ");
                        writer.newLine();
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 3 runs ");
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter2;
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter1;
                        }
                        ballsRemaining--;
                        break;

                    }

                    case "4": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 4;
                        currentStriker.addRuns(4);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 4 runs ");
                        writer.newLine();
                        System.out.println(YELLOW + "FOUR" + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 4 runs ");
                        ballsRemaining--;
                        break;

                    }
                    case "6": {
                        currentBall++;
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                            System.out.println("OVER CHANGE ");
                            if (currentStriker == currentBatter1) {
                                currentStriker = currentBatter2;
                            } else if (currentStriker == currentBatter2) {
                                currentStriker = currentBatter1;
                            }
                        }
                        score += 6;
                        currentStriker.addRuns(6);
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " scored 6 run ");
                        writer.newLine();
                        System.out.println(YELLOW + "SIXER" + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);

                        System.out
                                .println(currentOver + "." + currentBall + " " + currentStriker.getName()
                                        + " scored 6 runs ");
                        ballsRemaining--;
                        break;

                    }
                    case "W": {
                        currentBall++;
                        wickets++;
                        currentBowler.addWickets();
                        if (currentBall == 6) {
                            currentOver++;
                            currentBall = 0;
                        }
                        System.out.println(currentStriker.getName() + " got out at " + currentStriker.getRunsScored() + " by " + currentBowler.getName());
                        if (wickets == team1Players.size() - 1) {
                            break;
                        }
                        if (currentStriker == currentBatter1) {
                            currentStriker = currentBatter1 = currentTeam.pop();
                        } else if (currentStriker == currentBatter2) {
                            currentStriker = currentBatter2 = currentTeam.pop();
                        }
                        writer.write("Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall + " "
                                + currentStriker.getName() + " got out at " + currentStriker.getRunsScored() + " by "
                                + currentBowler.getName());
                        writer.newLine();
                        ballsRemaining--;
                        break;

                    }
                    case "Nb": {
                        score++;
                        System.out.println(RED + "NO BALL " + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " NO BALL ");
                        writer.newLine();
                        break;

                    }
                    case "Wd": {
                        score++;
                        System.out.println(RED + "WIDE BALL " + RESET);
                        System.out.println("\n");
                        System.out.println(GREEN + "Score " + score + "/" + wickets + RESET);
                        writer.write(
                                "Score " + score + "/" + wickets + "    " + currentOver + "." + currentBall
                                        + " WIDE BALL ");
                        writer.newLine();
                        break;

                    }

                    default:
                        System.out.println(RED + "Enter valid outcome " + RESET);
                }

            }
            System.out.println("\n");
             // Print the final score of first innings and target for second innings
            if (teamTableName == "team1_players1") {
                writer1.newLine();
                writer2.newLine();
                writer1.write(write1);
                writer2.write(write2);
                writer1.newLine();
                writer2.newLine();
                
            } else if (teamTableName == "team2_players1") {
                writer1.newLine();
                writer2.newLine();
                writer2.write(write1);
                writer1.write(write2);
                writer1.newLine();
                writer2.newLine();
            }

            // Iterate through the batting team players to update wicketsTaken in the database and write them to the file
            for (int i = 1; i <= team1Players.size(); i++) {
                String sql = "UPDATE " + teamTableName + " SET runsScored = ? WHERE pid = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, currentTeamList.get(i - 1).getRunsScored());
                pst.setInt(2, i);
                pst.executeUpdate();

                if (teamTableName == "team1_players1") {
                    String write = String.format("%15s %15s", currentTeamList.get(i - 1).getName(),
                            currentTeamList.get(i - 1).getRunsScored());
                    writer1.write(write);
                    writer1.newLine();
                } else if (teamTableName == "team2_players1") {
                    String write = String.format("%15s %15s", currentTeamList.get(i - 1).getName(),
                            currentTeamList.get(i - 1).getRunsScored());
                    writer2.write(write);
                    writer2.newLine();
                }
            }

            // Iterate through the bowling team players to update wicketsTaken in the database and write them to the file
            for (int i = 1; i <= oppositionTeamList.size(); i++) {
                String sql = "UPDATE " + oppTeamTableName + " SET wicketsTaken = ? WHERE pid = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, oppositionTeamList.get(i - 1).getWicketsTaken());
                pst.setInt(2, i);
                pst.executeUpdate();

                // Write bowling player details to the respective file
                String write = String.format("%15s %15s",
                        oppositionTeamList.get(i - 1).getName(),
                        oppositionTeamList.get(i - 1).getWicketsTaken());

                if (oppTeamTableName.equals("team1_players1")) {
                    writer1.write(write);
                    writer1.newLine();
                } else if (oppTeamTableName.equals("team2_players1")) {
                    writer2.write(write);
                    writer2.newLine();
                }
            }

            // To find which team won the match
            if (score < target - 1 || ballsRemaining == 0 || (wickets == team1Players.size() - 1)) {
                // The match is lost.
                if (teamTableName.equals("team1_players1")) {
                    System.out.println(GREEN + "Team " + getTeam2Name() + " Won By " + (target - score - 1) + " Runs");
                    writer.newLine();
                    writer.write("Team " + getTeam2Name() + " Won By " + (target - score - 1) + " Runs");
                } else if (teamTableName.equals("team2_players")) {
                    System.out.println(GREEN + "Team " + getTeam1Name() + " Won By " + (target - score - 1) + " Runs");
                    writer.write("Team " + getTeam1Name() + " Won By " + (target - score - 1) + " Runs");
                }
            } else if (score >= target) {
                // The match is won.
                if (teamTableName.equals("team1_players1")) {
                    System.out.println(
                            GREEN + "Team " + getTeam1Name() + " Won By " + (team2Players.size() - wickets)
                                    + " Wickets");
                    writer.newLine();
                    writer.write("Team " + getTeam1Name() + " Won By " + (team2Players.size() - wickets) + " Wickets");
                } else if (teamTableName.equals("team2_players1")) {
                    System.out.println(
                            GREEN + "Team " + getTeam2Name() + " Won By " + (team1Players.size() - wickets)
                                    + " Wickets");
                    writer.newLine();
                    writer.write("Team " + getTeam1Name() + " Won By " + (team2Players.size() - wickets) + " Wickets");
                }
            } else {
                // The match is tied.
                System.out.println(GREEN + "Match Tied!");
                writer.newLine();
                writer.write("Match Tied!");
            }

            writer1.close();
            writer2.close();
            writer.close();

            System.out.println(RESET + "Thank You for using this system..........");
        } catch (IOException e) {
            System.out.println("IO Error " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }

    }
}

class Main {
    public static String YELLOW = "\u001B[33m";
    public static String RED = "\u001B[31m";
    public static String RESET = "\u001B[0m";

    public static void main(String[] args) {
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            // Connection to database
            Class.forName(driver);
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/cricket", "root", "root");
            String sql = "Truncate table team1_players1";
            String sql1 = "Truncate table team2_players1";
            Statement st = con.createStatement();
            st.execute(sql);
            st.execute(sql1);

            Scanner sc = new Scanner(System.in);
            System.out.println(YELLOW + "Enter name of Team 1" + RESET);
            String t1 = sc.nextLine();
            System.out.println(YELLOW + "Enter name of Team 2" + RESET);
            String t2 = sc.nextLine();

            Match match = new Match(t1, t2);

            int num_players = 0;
            while (true) {
                try {
                    num_players = getIntInput(sc, YELLOW + "Number of players " + RESET);
                    break; // Exit the loop if the input is valid
                } catch (InputMismatchException e) {
                    System.err.println("Invalid input for number of players. Please enter a valid number.");
                    sc.nextLine(); // Consume the invalid input
                }
            }

            match.addPlayers(num_players);

            System.out.println();
            System.out.println("TOSS TIME");
            int toss_winner = 0;

            toss_winner = match.toss();

            if (toss_winner == 1) {
                System.out.println(YELLOW + "Team 1 is Batting" + RESET);
                try {
                    match.innings1(toss_winner);
                    match.innings2(toss_winner + 1);
                } catch (Exception e) {
                    System.err.println("An error occurred during innings: " + e.getMessage());
                    System.exit(1);
                }
            } else if (toss_winner == 2) {
                System.out.println(YELLOW + "Team 2 is Batting" + RESET);
                try {
                    match.innings1(toss_winner);
                    match.innings2(toss_winner - 1);
                } catch (Exception e) {
                    System.err.println("An error occurred during innings: " + e.getMessage());
                    System.exit(1);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int getIntInput(Scanner sc, String prompt) {
        int input = 0;
        while (true) {
            try {
                System.out.println(prompt);
                input = sc.nextInt();
                sc.nextLine();
                if (input > 1) {
                    break; // Exit the loop if the input is valid
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a valid number.");
                sc.nextLine(); // Consume the invalid input
            }
        }
        return input;
    }
}
