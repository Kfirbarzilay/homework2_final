package homework2;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import homework2.Utils.Result;


/**
 * This class implements a testing driver which reads test scripts
 * from files for testing Graph and PathFinder.
 */
public class TestDriver {

	// String -> Graph: maps the names of graphs to the actual graph
	private final Map<String,Graph<WeightedNode>> graphs = new HashMap<>();
	// String -> WeightedNode: maps the names of nodes to the actual node
	private final Map<String,WeightedNode> nodes = new HashMap<>();
	private final BufferedReader input;
	private final PrintWriter output;


	/**
	 * Creates a new TestDriver.
	 */
	public TestDriver(Reader r, Writer w) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);
	}


	/**
	 * Executes the commands read from the input and writes results to the output.
	 */
	public void runTests() throws IOException {

		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			// echo blank and comment lines
			if (inputLine.trim().length() == 0 ||
				inputLine.charAt(0) == '#') {
				output.println(inputLine);
				continue;
			}

			// separate the input list on white space
			StringTokenizer st = new StringTokenizer(inputLine);
			if (st.hasMoreTokens()) {
				String command = st.nextToken();

				List<String> arguments = new ArrayList<>();
				while (st.hasMoreTokens())
					arguments.add(st.nextToken());

				executeCommand(command, arguments);
			}
		}

		output.flush();
	}


	private void executeCommand(String command, List<String> arguments) {

		try {
			if (command.equals("CreateGraph")) {
				createGraph(arguments);
			} else if (command.equals("CreateNode")) {
				createNode(arguments);
			} else if (command.equals("AddNode")) {
				addNode(arguments);
			} else if (command.equals("AddEdge")) {
				addEdge(arguments);
			} else if (command.equals("ListNodes")) {
				listNodes(arguments);
			} else if (command.equals("ListChildren")) {
				listChildren(arguments);
			} else if (command.equals("FindPath")) {
				findPath(arguments);
			} else if (command.equals("DfsAlgorithm")){
				dfsAlgorithm(arguments);
			}else {
				output.println("Unrecognized command: " + command);
			}
		} catch (Exception e) {
			output.println("Exception: " + e.toString());
		}
	}

	private void createGraph(List<String> arguments) {

		if (arguments.size() != 1)
			throw new CommandException(
				"Bad arguments to CreateGraph: " + arguments);

		String graphName = arguments.get(0);
		createGraph(graphName);
	}

  /**
   * Creates a new graph for testing
   */
	private void createGraph(String graphName) {

		assert graphName != null;
		//creating the graph using the name ctor
		graphs.put(graphName,new Graph<WeightedNode>(graphName));
		output.println(("created graph " + graphName));
	}


	private void createNode(List<String> arguments) {

		if (arguments.size() != 2)
			throw new CommandException(
				"Bad arguments to createNode: " + arguments);

		String nodeName = arguments.get(0);
		String cost = arguments.get(1);
		createNode(nodeName, cost);
	}

	 /**
	 * Creates a new node for testing .Notice that it does not yet belong to any graph.
	 */
	private void createNode(String nodeName, String cost) {

		 assert nodeName != null && cost != null && (new Integer(cost)).intValue() >= 0;
		 // create a new WeightedNode out of the name and "atoi" of the string cost
		 nodes.put(nodeName,new WeightedNode(nodeName,(new Integer(cost)).intValue()));
		 output.println("created node " + nodeName + " with cost " + cost);
	}


	private void addNode(List<String> arguments) {

		if (arguments.size() != 2)
			throw new CommandException(
				"Bad arguments to addNode: " + arguments);

		String graphName = arguments.get(0);
		String nodeName = arguments.get(1);
		addNode(graphName, nodeName);
	}

	/**
	 * Adds an existing node nodeName to an existing graph named graphName .
	 */
	private void addNode(String graphName, String nodeName) {

		//@requires: graphName != null , nodeName!= null;
		 assert graphName != null && nodeName != null;

		 Graph<WeightedNode> currentGraph = graphs.get(graphName);
		 WeightedNode currentNode = nodes.get(nodeName);

		 // check that the get successfully finished
		 assert currentGraph != null && currentNode != null ;

		 //check that addNode is successful
		 if(currentGraph.addNode(currentNode, nodeName) == Result.itemAlreadyExists){
			 System.err.println("The name given is not unique, addNode requires not met");
			 return;
		 }

		 output.println("added node " + nodeName +" to " + graphName);

	}


	private void addEdge(List<String> arguments) {

		if (arguments.size() != 3)
			throw new CommandException(
				"Bad arguments to addEdge: " + arguments);

		String graphName = arguments.get(0);
		String parentName = arguments.get(1);
		String childName = arguments.get(2);
		addEdge(graphName, parentName, childName);
	}

	/**
	 * Adds a new edge to an existing graph graphName: parentName -> childName
	 */
	private void addEdge(String graphName, String parentName, String childName) {

		 assert graphName != null && parentName != null && childName != null;

		 Graph<WeightedNode> graph = graphs.get(graphName);
		 WeightedNode parent = nodes.get(parentName);
		 WeightedNode child = nodes.get(childName);
		 //check that the get succeeded
		 assert graph != null && parent != null && child != null;

		// Check that addNode is successful
		 Result res = graph.addEdge(parentName, childName);
		 if (res == Result.itemDoesntExist){
			System.err.println("The names given are not in the graph: " + graph.getName());
			 return;
		 }else if (res == Result.itemAlreadyExists){
			System.err.println("The edge given already exists at " + graph.getName());
			 return;
		 }else if (res == Result.invalidArguments){
			System.err.println("The name given arguments are invalid graph addEdge requires not met");
			 return;
		 }else{ // huge success
			 output.println("added edge from " + parentName + " to " + childName + " in " + graphName);
		 }
	}


	private void listNodes(List<String> arguments) {

		if (arguments.size() != 1)
			throw new CommandException(
				"Bad arguments to listNodes: " + arguments);

		String graphName = arguments.get(0);
		listNodes(graphName);
	}

	/**
	 * Prints to the output the list of the nodes in the graph named graphName
	 */
	private void listNodes(String graphName) {
		assert graphName != null;
		Graph<WeightedNode> graph = graphs.get(graphName);
		if(graph == null){
			System.out.println("graph name is: " + graphName);
		}
		assert graph != null;
		String s = graph.getNodesString();
		assert s != null;
		output.println(s);

	}

	private void listChildren(List<String> arguments) {

		if (arguments.size() != 2)
			throw new CommandException(
				"Bad arguments to listChildren: " + arguments);

		String graphName = arguments.get(0);
		String parentName = arguments.get(1);
		listChildren(graphName, parentName);
	}

	/**
	 * Prints to the output the list children of the node in the graph, if both of them exist.
	 */
	private void listChildren(String graphName, String parentName) {
		assert graphName != null && parentName != null;
		Graph<WeightedNode> graph = graphs.get(graphName);
		assert graph != null;
		String s = graph.getChildrenString(parentName);
		assert s != null;
		output.println(s);

	}


	private void findPath(List<String> arguments) {

		String graphName;
		List<String> sourceArgs = new ArrayList<>();
		List<String> destArgs = new ArrayList<>();

		if (arguments.size() < 1)
			throw new CommandException("Bad arguments to FindPath: " + arguments);

		Iterator<String> iter = arguments.iterator();
		graphName = iter.next();

		// Extract source arguments
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.equals("->"))
				break;
			sourceArgs.add(s);
		}

		// Extract destination arguments
		while (iter.hasNext())
			destArgs.add(iter.next());

		if (sourceArgs.size() < 1)
			throw new CommandException(
				"Too few source args for FindPath");

		if (destArgs.size() < 1)
			throw new CommandException(
				"Too few dest args for FindPath");

		findPath(graphName, sourceArgs, destArgs);
	}

	/**
	 * Finds path minimal nodes path between sources nodes and destinations nodes.
	 */
	private void findPath(String graphName, List<String> sourceArgs, List<String> destArgs) {
		// Get graph
		Graph<WeightedNode> g = graphs.get(graphName);
		Iterator<GraphNode<WeightedNode>> initIter = g.getNodes();

		while (initIter.hasNext())
		{
			WeightedNode currNode = initIter.next().getNode();
			currNode.setColor("White");
			currNode.initNumofBackEdges();
		}

		// Create a PathFinder instance and call findShortestPath
		PathFinder<GraphNode<WeightedNode>,WeightedNode,WeightedNodePath> P = new PathFinder<>();
		NodeCountingPath shortestPath =  P.findShortestPath(g, sourceArgs, destArgs);

		// Print the shortest path
		if(shortestPath == null){
			// No path was found
			output.println(String.format("no path found in %s",g.getName()));
		}
		else
		{
			String s = String.format("found path in %s:",g.getName());
			Iterator<WeightedNode> iter = shortestPath.iterator();
			while(iter.hasNext()){
				s += " " + iter.next().getName();
			}
			s += " with cost " + (int)shortestPath.getCost();
			output.println(s);
		}
	}

	private void dfsAlgorithm(List<String> arguments) {
		String graphName, startNode, endNode;

		// Check number of arguments
		if (arguments.size() < 2 || arguments.size() > 3)
			throw new CommandException("Bad arguments to dfsAlgorithm: " + arguments);

		Iterator<String> iter = arguments.iterator();
		graphName = iter.next();

		// Get the start node
		startNode =  iter.next();

		// If end node has given, get the last node and call the suitable method
		if (arguments.size() == 2)
		{
			dfsAlgorithm(graphName, startNode);

		}
		else
		{
			endNode = iter.next();
			dfsAlgorithm(graphName, startNode, endNode);
		}


	}

	/**
	 * Prints route of the DFS run from source node to destination node.
	 */
	private void dfsAlgorithm(String graphName, String sourceArg, String destArg) {

		// Get graph
		Graph<WeightedNode> graph = graphs.get(graphName);

		// Create dfsAlgorithm instance
		DfsAlgorithm dfs = new DfsAlgorithm(graph, sourceArg);
		// Run DFS and get the string array
		LinkedList<String> dfsPath = dfs.callDfs(destArg);


		// Print the DFS path from source to dest
		String s1 = String.format("dfs algorithm output %s %s -> %s:", graph.getName(), sourceArg, destArg);
		if(dfsPath == null){
			output.println( s1.concat(" no path was found"));
		}
		else
		{
			String s2 = new String();
			Iterator<String> iter = dfsPath.iterator();
			while(iter.hasNext()){
				s2 += " " + iter.next();
			}
			output.println(s1.concat(s2));
		}
	}

	/**
	 * Prints route of the DFS run from source node until walking over the tree.
	 */
	private void dfsAlgorithm(String graphName, String sourceArg) {
		// Get graph
		Graph<WeightedNode> graph = graphs.get(graphName);

		// Create dfsAlgorithm instance
		DfsAlgorithm dfs = new DfsAlgorithm(graph, sourceArg);
		// Run DFS and get the string array
		LinkedList<String> dfsPath = dfs.callDfs();

		// Print the DFS path from source to dest
		String s1 = String.format("dfs algorithm output %s %s:", graph.getName(), sourceArg);

		// There always be an output because at list source node is part of the graph and the list
		String s2 = new String();
		Iterator<String> iter = dfsPath.iterator();
		while(iter.hasNext()){
			s2 += " " + iter.next();
		}
		output.println(s1.concat(s2));

	}


	private static void printUsage() {
		System.err.println("Usage:");
		System.err.println("to read from a file: java TestDriver <name of input script>");
		System.err.println("to read from standard input: java TestDriver");
	}


	public static void main(String args[]) {

		try {
			// check for correct number of arguments
			if (args.length > 1) {
				printUsage();
				return;
			}

			TestDriver td;
			if (args.length == 0)
				// no arguments - read from standard input
				td = new TestDriver(new InputStreamReader(System.in),
									new OutputStreamWriter(System.out));
			else {
				// one argument - read from file
				java.nio.file.Path testsFile = Paths.get(args[0]);
				if (Files.exists(testsFile) && Files.isReadable(testsFile)) {
					td = new TestDriver(
							Files.newBufferedReader(testsFile, Charset.forName("US-ASCII")),
							new OutputStreamWriter(System.out));
				} else {
					System.err.println("Cannot read from " + testsFile.toString());
					printUsage();
					return;
				}
			}

			td.runTests();

		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}
}


/**
 * This exception results when the input file cannot be parsed properly.
 */
class CommandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CommandException() {
		super();
	}

	public CommandException(String s) {
		super(s);
	}
}