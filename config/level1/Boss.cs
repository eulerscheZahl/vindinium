using System;
using System.Linq;
using System.Collections.Generic;

class Player
{
	static void Main (string[] args)
	{
		int size = int.Parse (Console.ReadLine ());
		Console.Error.WriteLine (size);
		Board board = new Board (size);
		int myID = int.Parse (Console.ReadLine ()); // ID of your hero
		Console.Error.WriteLine (myID);

		// game loop
		while (true) {
			int entityCount = int.Parse (Console.ReadLine ()); // the number of entities
			Console.Error.WriteLine (entityCount);
			List<Hero> heroes = new List<Hero> ();
			for (int i = 0; i < entityCount; i++) {
				string[] inputs = Console.ReadLine ().Split (' ');
				Console.Error.WriteLine (string.Join (" ", inputs));
				string entityType = inputs [0]; // HERO or MINE
				int id = int.Parse (inputs [1]); // the ID of a hero or the owner of a mine
				int x = int.Parse (inputs [2]); // the x position of the entity
				int y = int.Parse (inputs [3]); // the y position of the entity
				int life = int.Parse (inputs [4]); // the life of a hero (-1 for mines)
				int gold = int.Parse (inputs [5]); // the gold of a hero (-1 for mines)
				if (entityType == "HERO") {
					heroes.Add (new Hero (id, x, y, life, gold));
				} else if (entityType == "MINE") {
					board.Grid [x, y].Owner = heroes.FirstOrDefault (h => h.ID == id);
				}
			}

			Hero me = heroes.Find (h => h.ID == myID);
			Node mePos = board.Grid [me.X, me.Y];
			List<Node> nodes = board.allNodes;
			int[] dist = Distances (mePos, nodes);
			for (int i = 0; i < nodes.Count; i++) {
				nodes [i].Dist = dist [i];
			}

			List<Node> candidates = candidates = nodes.Where (n => n.Mine && n.Owner != me).ToList ();
			Node closestMine = null;
			if (candidates.Count > 0)
				closestMine = candidates.OrderBy (c => c.Dist).First ();

			if (closestMine == null)
				Console.WriteLine ("WAIT");
			else {
				dist = Distances (closestMine, nodes);
				for (int i = 0; i < nodes.Count; i++) {
					nodes [i].Dist = dist [i];
				}
				foreach (Node next in board.Grid[me.X, me.Y].Neighbors) {
					if (next.Dist == mePos.Dist - 1 && (next.Dist == 0 || next.Free)) {
						if (next.X < me.X)
							Console.WriteLine ("WEST");
						if (next.X > me.X)
							Console.WriteLine ("EAST");
						if (next.Y < me.Y)
							Console.WriteLine ("NORTH");
						if (next.Y > me.Y)
							Console.WriteLine ("SOUTH");
						break;
					}
				}
			}
		}
	}

	static int[] Distances (Node start, List<Node> nodes)
	{
		int[] result = new int[nodes.Count];
		for (int i = 0; i < result.Length; i++) {
			result [i] = -1;
		}
		Queue<Node> queue = new Queue<Node> ();
		queue.Enqueue (start);
		result [start.ID] = 0;
		while (queue.Count > 0) {
			Node n = queue.Dequeue ();
			if (!n.Free && n != start)
				continue;
			foreach (Node m in n.Neighbors) {
				if (result [m.ID] != -1)
					continue;
				result [m.ID] = result [n.ID] + 1;
				queue.Enqueue (m);
			}
		}

		return result;
	}

	static int[,] offset = new int[,] {
		{ 0, 1 },
		{ 1, 0 },
		{ 0, -1 },
		{ -1, 0 }
	};

	class Board
	{
		public Node[,] Grid;
		public int Width;
		public int Height;
		public List<Node> allNodes = new List<Node> ();

		public Board (int size)
		{
			this.Width = size;
			this.Height = size;
			Grid = new Node[size, size];
			for (int y = 0; y < size; y++) {
				string line = Console.ReadLine ();
				Console.Error.WriteLine (line);
				for (int x = 0; x < size; x++) {
					Grid [x, y] = new Node (allNodes.Count, x, y, line [x]);
					if (!Grid [x, y].Blocked)
						allNodes.Add (Grid [x, y]);
				}
			}

			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					for (int dir = 0; dir < 4; dir++) {
						int x_ = x + offset [dir, 0];
						int y_ = y + offset [dir, 1];
						if (x_ < 0 || x_ >= size || y_ < 0 || y_ >= size || Grid [x_, y_].Blocked)
							continue;
						Grid [x, y].Neighbors.Add (Grid [x_, y_]);
					}
				}
			}
		}
	}

	class Node
	{
		public int ID;
		public List<Node> Neighbors = new List<Node> ();
		public bool Blocked;
		public bool Free;
		public bool Tavern;
		public bool Mine;
		public Hero Owner;
		public int Dist;
		public int X, Y;

		public Node (int id, int x, int y, char c)
		{
			this.X = x;
			this.Y = y;
			this.ID = id;
			Blocked = c == '#';
			Mine = c == 'M';
			Tavern = c == 'T';
			Free = "0123.".Contains(c);
		}
	}

	class Hero
	{
		public int ID;
		public int X, Y;
		public int Life;
		public int Gold;

		public Hero (int id, int x, int y, int life, int gold)
		{
			this.ID = id;
			this.X = x;
			this.Y = y;
			this.Life = life;
			this.Gold = gold;
		}
	}
}
