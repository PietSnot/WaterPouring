package waterpouring3;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class WaterPouring3 {

    public static void main(String[] args) {
        WaterPouring3 wp3 = new WaterPouring3(2,5, 11);
        for (int i = 1; i <= 19; i++) System.out.println(wp3.solve(i));
        WaterPouring3 wp = new WaterPouring3(2, 5, 5);
        for (int i = 1; i <= 13; i++) System.out.println(wp.solve(i));
        wp = new WaterPouring3(2);
        for (int i = 0; i <= 3; i++) System.out.println(wp.solve(i));
    }
    
    //*******************************************************************
    // members
    //*******************************************************************
    List<Integer> volumes = new ArrayList<>();
    List<Move> moves = new ArrayList<>();
    
    //*******************************************************************
    // constructor
    //*******************************************************************
    public WaterPouring3(int... volumes) {
        
        if (volumes.length == 0) {
            throw new IllegalArgumentException("Please specify decent volumes!");
        }
        for (int i : volumes) this.volumes.add(i);

        for (int glassnr = 0; glassnr < volumes.length; glassnr++) {
            moves.add(new Empty(glassnr));
            moves.add(new Fill(glassnr));
            for (int glassnr2 = 0; glassnr2 < volumes.length; glassnr2++) {
                if (glassnr != glassnr2) moves.add(new Pour(glassnr, glassnr2));
            }
        }
    }
    
    //*******************************************************************
    // the Scala 'Move' classes
    //*******************************************************************
    interface Move {
        public List<Integer> change(List<Integer> list);
    }
    //-------------------------------------------------------------
    class Empty implements Move {
        int glassnr;
        //--------------------------------------------------
        public Empty(int glassnr) {this.glassnr = glassnr;}
        //--------------------------------------------------
        public List<Integer> change(List<Integer> list) {
            List<Integer> newlist = new ArrayList<>(list);
            newlist.set(glassnr, 0);
            return newlist;
        }
    }
    //-------------------------------------------------------------
    class Fill implements Move {
        int glassnr;
        //--------------------------------------------------
        public Fill(int glassnr) {this.glassnr = glassnr;}
        //--------------------------------------------------
        public List<Integer> change(List<Integer> list) {
            List<Integer> newlist = new ArrayList<>(list);
            newlist.set(glassnr, volumes.get(glassnr));
            return newlist;
        }
    }
    //--------------------------------------------------------------
    class Pour implements Move {
        int from, to;
        //----------------------------
        public Pour(int from, int to) {this.from = from; this.to = to;}
        //----------------------------
        public List<Integer> change(List<Integer> list) {
            List<Integer> newlist = new ArrayList<>(list);
            int pour = Math.min(list.get(from), volumes.get(to) - list.get(to));
            newlist.set(from, list.get(from) - pour);
            newlist.set(to, list.get(to) + pour);
            return newlist;
        }
    }
    
    //********************************************************************
    // the Path class
    //********************************************************************
    class Path {
        List<Move> history;
        List<Integer> endState;
        //-------------------------------------
        public Path() {
            history = new ArrayList<>();
            endState = 
                volumes.stream().map(e -> 0).collect(Collectors.toList());
        }
        //-------------------------------------
        public Path(List<Move> moves, List<Integer> endstate) {
            history = new ArrayList<>(moves);
            endState = new ArrayList<>(endstate);
        }
        //-------------------------------------
        private Path addMove(Move move) {
            List<Move> newhistory = new ArrayList<>(history);
            newhistory.add(move);
            return new Path(newhistory, move.change(endState));
        }
        //-------------------------------------
        public List<Path> generateNewPaths(List<List<Integer>> oldStates) {
            List<Path> newPaths = 
                moves.stream()
                .map(move -> addMove(move))
                .filter(path -> !oldStates.contains(path.endState))
                .collect(Collectors.toList())
            ;
            newPaths.forEach(path -> oldStates.add(path.endState));
            return newPaths;
        }
        //-------------------------------------
        public boolean contains(int target) {
            return target == endState.stream().reduce(0, (a, b) -> a + b);
        }
        //-------------------------------------
        @Override
        public String toString() {
            List<Integer> initialState = 
                volumes.stream().map(e -> 0).collect(Collectors.toList());
            if (history.isEmpty()) return initialState.toString();
            List<List<Integer>> states = new ArrayList<>();
            states.add(initialState);
            history.stream().reduce(
                initialState, 
                (state, move) -> { 
                    List<Integer> newstate = move.change(state); 
                    states.add(newstate); 
                    return newstate;
                }, 
                (List<Integer> x, List<Integer> y) -> y   // is not used, so doesn't matter
            );
//            IntStream.range(0, history.size())
//                .forEach(i -> states.add(history.get(i).change(states.get(i))));
            return states.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(System.lineSeparator()))
            ;
        }
    }
    
   //********************************************************************
    // solving!
    //********************************************************************
    public String solve(int target) {
        String s0 = "****************************************\n";
        String s1 = "given volumes: " + volumes + "\n";
        String s2 = "target: " + target + "\n";
        String s3 = "-------------------------\n";
        int max = volumes.stream().reduce(0, (a, b) -> a + b);
        if (target > max) {
            return s0 + s1 + s2 + s3 + "target: " + target + " is too big.";
        }
        Path initialPath = new Path();
        List<List<Integer>> oldStates = new ArrayList<>();
        LinkedList<Path> queue = new LinkedList<>();
        oldStates.add(initialPath.endState);
        queue.add(initialPath);
        String answer = "No sultion found.";
        //-------------------------------------
        while (!queue.isEmpty()) {
            Path p = queue.pollFirst();
//            System.out.println("temp: " + p.endState);
            if (p.contains(target)) {
                 answer = p.toString();
                 queue.clear();
            }
            else {
                queue.addAll(p.generateNewPaths(oldStates));
            }
        }
        return s0 + s1 + s2 + s3 + answer;
    }
}
