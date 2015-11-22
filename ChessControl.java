import java.util.ArrayList;
import java.util.HashMap;

public class ChessControl implements ChessViewerControl, ChessListener {

	private final static HashMap<String, String> rules = new HashMap<String, String>() {
		{
			put("castling",
					"Only under those circumstances, you can castling\n"
							+ "1.Your king and the corresponding rook has never been moved.\n"
							+ "2.There is no chessman between your king and rook.\n"
							+ "3.The squres that your king goes over should not under attack by any pieces of the opponent.\n"
							+ "4.Your king cannot be in check either before and after the castling.");
			put("pawn",
					"The pawn may move forward to the unoccupied square immediately in front of it on the same file without capturing, "
							+ "or advance two squares along the same file without capturing on its first move;"
							+ "or capture an opponent's piece on a square diagonally in front of it on an adjacent file.\n"
							+ "En Passant and promotion are also special rules for pawn.");
			put("king", "The king moves one square in any direction. You always want to protect your king.\n"
					+ "Castling is also a special rule for king.");
			put("queen",
					"The queen combines the power of the rook and bishop and can move any number of squares along rank, file, or diagonal, without going over any pieces");
			put("rook", "The rook can move any number of squares along any rank or file without going over any pieces");
			put("bishop", "The bishop can move any number of squares diagonally, without going over any pieces.");
			put("knight",
					"The knight moves to any of the closest squares that are not on the same rank, file, or diagonal, thus the move forms an \"L\"-shape:");
			put("en passant", "En passant move:\n"
					+ "When a player moves a pawn 2 squares then on the very next move, the other player moves their pawn diagonally forward 1 square to the square that pawn moved through, capturing it in the process, the latter is said to be doing en passant. "
					+ "Note that the pawn does not move to the square of the pawn it captured in en passant.\n");
			put("promotion",
					"When a pawn reaches its eighth rank, it is immediately changed into the player's choice of a queen, knight, rook, or bishop of the same color.");
		}
	};

	ChessViewer view;
	Chess chess;
	Piece chosen;

	/**
	 * start my little chess game!!!!
	 * 
	 * @param args
	 *            ignored
	 */
	public ChessControl() {
		chess = new Chess();
		chess.addChessListener(this);
		chosen = null;

		view = new ChessViewer(this);
		view.setVisible(true);
		view.pack();
		updateAll();
	}

	public void restart() {
		chess.removeChessListener(this);
		chess = new Chess();
		chess.addChessListener(this);
		chosen = null;
		updateAll();
	}

	private void updateAll() {
		for (Square s : chess.getAllSquares())
			updateSquare(s);
	}

	/**
	 * return the requested rules text.
	 * 
	 * @param command
	 * @return
	 */
	private String rules(String command) {
		if (rules.containsKey(command))
			return rules.get(command);
		return "You can get rules for castling, pawn, king, queen, rook, bishop, knight, En Passant, promotion";
	}

	/**
	 * handle command and call the relevant method
	 * 
	 * @param s
	 *            the input string
	 * @param chess
	 *            the game class
	 * @return the next output line
	 */
	public void handleCommand(String command) {
		String c = command.toLowerCase();
		if (c.equals("print")) {
			printRecords();
		} else if (c.equals("quit")) {
			view.dispose();
			System.exit(0);
		} else if (c.equals("restart")) {
			restart();
			view.printOut("Start a new game!");
		} else if (c.startsWith("rules for ")) {
			view.printOut(rules(c.substring(10)));
		} else if (chess.hasEnd()) {
			view.printOut(chess.lastMoveDiscript());
		} else if (c.length() == 0) {
			// ret = "";
		} else if (c.equals("help")) {
			view.printOut("Enter commands:\n" + "enter 'undo' to undo the previous round;\n"
					+ "enter 'restart' to start a new game over;\n'" + "enter 'print' to print all the records;\n"
					+ "enter 'resign' to give up;\n" + "enter 'draw' to request for draw;\n"
					+ "enter complete or abbreviated algebraic chess notation to make a move;\n"
					+ "enter 'rules for ....' to get help about the rules of chess.\n"
					+ "    Castling, Pawn, King, Queen, Rook, Bishop, Knight, En Passant, Promotion.");
		} else if (c.equals("undo")) {
			view.printOut(chess.undoPreviousMove());
		} else if (c.equals("resign")) {
			view.printOut(chess.resign());
		} else if (c.equals("draw")) {
			view.printOut(chess.askForDraw());
		} else if (c.charAt(0) == 'o') {
			view.printOut(chess.castling(c));
		} else if (c.length() == 6 || c.length() == 5) {
			view.printOut(makeMove(c));
		} else if (c.length() < 5) {
			String fullStr = figureOutTheAbbreviation(c);
			if (fullStr != null)
				if (fullStr.startsWith("A"))
					view.printOut(fullStr);
				else
					handleCommand(fullStr);
			else
				view.printOut("Incorrect format of abbreviation command.\n"
						+ "You can omit the start spot of the move in the complete command.");
		} else {
			view.printOut(
					"Please enter the move as (The type of chessman)(the start position)(its action)(the end position)\n"
							+ "you can omit the \"P\" at the begining for a pawn."
							+ "for casting, enter \"O-O\" or \"O-O-O\"\n" + "for examples, \"e2-e4\", \"Nb2-c3\" ");
		}

	}

	/**
	 * print out the records of the game in starndart chess recording language
	 * 
	 * @return records
	 */
	public void printRecords() {
		ArrayList<Move> records = chess.getRecords();
		if (records.size() == 0)
			view.printOut("Game hasn't started yet.");
		String s = "";
		for (Move i : records) {
			s += i.print();
		}
		view.printOut(s);
	}

	/**
	 * This method will be called, if the user types a command to make a move.
	 * 
	 * Interpret the command, and find out if it is legal to do make this move.
	 * If it is, make this move.
	 * 
	 * @param s
	 *            the input command
	 * @return
	 */
	public String makeMove(String s) {
		char type;
		if (s.length() == 5) {
			s = 'P' + s;
			type = 'P';
		} else {
			type = s.toUpperCase().charAt(0);
			if (!(type == 'R' || type == 'N' || type == 'B' || type == 'Q' || type == 'K' || type == 'P'))
				return "Please enter valid initial of chessman -- R(Root), N(Knight), B(Bishop), Q(Queen), K(King). If you omit it, it is assumed as Pawn.";
		}

		s = s.toLowerCase();
		Square start = chess.getSquare(s.substring(1, 3));
		if (start == null)
			return "please enter a valid start Position";

		boolean takeOrNot;
		char action = s.charAt(3);
		if (action == '-')
			takeOrNot = false;
		else if (s.charAt(3) == 'x')
			takeOrNot = true;
		else
			return "Pleae enter \"-\" or \"x\" to indicate whether this move takes some piece or not.";

		Square end = chess.getSquare(s.substring(4));
		if (end == null)
			return "please enter a valid end Position";

		Piece movedChessman = start.getPiece();
		if (movedChessman == null) {
			if (chess.getWhoseTurn())
				return "There should be a white chessman in the start Position!";
			else
				return "There should be a black chessman in the start Position!";
		}
		if (!(movedChessman.isType(type)))
			return "The chessman in the start Position is not corret! \n R(Root), N(Knight), B(Bishop), Q(Queen), K(King), omission for pawn";

		Piece chessmanTaken = end.getPiece();

		if (takeOrNot) {
			if (movedChessman.canCapture(end))
				return movedChessman.capture(end, chessmanTaken);
			else
				return "Illegal move! Please check the rule of " + movedChessman.getName() + "!";
		} else {
			if (chessmanTaken != null) {
				return "It works this time,but please use \"x\" if you want to take it next time. Thank you!\n"
						+ makeMove(s.replace('-', 'x'));
			}
			if (movedChessman.canMove(end))
				return movedChessman.move(end);
			else
				return "Illegal move! Please check the rule of " + movedChessman.getName() + "!";
		}
	}

	/**
	 * Tranformed the abbreviated command to the complete one, or return error
	 * if there is ambiguous about the abbreviated command.
	 * 
	 * @param s
	 * @return
	 */
	public String figureOutTheAbbreviation(String s) {
		char type = s.charAt(0);
		if (type == 'R' || type == 'N' || type == 'B' || type == 'Q' || type == 'K' || type == 'P')
			s = s.substring(1);
		else
			type = 'P';

		boolean takeOrNot = (s.charAt(0) == 'x');

		if (takeOrNot)
			s = s.substring(1);

		Square end = null;
		if (s.length() == 2)
			end = chess.getSquare(s);
		if (end == null)
			return null;

		ArrayList<Piece> possible = new ArrayList<Piece>();
		ArrayList<Piece> set;

		if (chess.getWhoseTurn())
			set = chess.getWhite();
		else
			set = chess.getBlack();

		if (takeOrNot) {
			for (Piece i : set) {
				if (i.isType(type) && i.canCapture(end))
					possible.add(i);
			}
		} else {
			for (Piece i : set) {
				if (i.isType(type) && i.canMove(end))
					possible.add(i);
			}
		}
		if (possible.size() == 0) {
			return "Ambiguity: No one can reach that spot.";
		} else if (possible.size() == 1) {
			String newStr = "" + type;
			newStr += possible.get(0).getP().toString();
			if (takeOrNot)
				newStr += "x";
			else
				newStr += "-";
			newStr += end.toString();
			return newStr;
		} else {
			return "Ambiguity: This can represent many different moves.";
		}
	}

	/**
	 * print out the result in the box.
	 */
	public void printInBox(String s) {
		view.printOut(s);
	}

	/**
	 * print out the temporal piece that is chosen in the box
	 * 
	 * @param s
	 */
	protected void printchosenPiece(String s) {
		if (s.charAt(0) == 'P')
			view.printTemp(s.substring(1));
		else
			view.printTemp(s);
	}

	/**
	 * clean the temporal piece information, because the user suggests a illegal
	 * move
	 */
	protected void printCleanTemp() {
		view.cleanTemp();
	}

	/**
	 * print out the outputs in the head label.
	 * 
	 * @param s
	 */
	public void printInLabel(String s) {
		view.setText(s);
	}

	@Override
	public void updateSquare(Square sq) {
		view.updateSquare(sq);
	}

	/**
	 * this mehod will be called if the user click on the board. It will find
	 * out whether the user has suggest a legal move, and provides proper
	 * outputs.
	 * 
	 * @param spot
	 *            the square that is clicked
	 */
	public void click(SquareLabel label) {
		Square spot = labelToSquare(label);
		if (chosen != null) {
			if (label.isHighLight() && !spot.equals(chosen.getP())) {
				String s = "";
				if (chosen.canMove(spot))
					s = chosen.move(spot);
				else if (chosen.canCapture(spot))
					s = chosen.capture(spot, spot.getPiece());

				printchosenPiece(chess.lastMoveOutPrint());
				printInBox(s);
				printInLabel(chess.lastMoveDiscript());
			} else
				printCleanTemp();
			deHighLightWholeBoard();
		} else {
			if (spot.occupiedBy(chess.getWhoseTurn())) {
				setChosenPiece(spot.getPiece());
				printchosenPiece(spot.getPiece().getType() + spot.toString());
			}
		}
	}

	/**
	 * when one possible piece is chosen, highlight it and all the spots it can
	 * move to.
	 * 
	 * @param piece
	 */
	public void setChosenPiece(Piece piece) {
		chosen = piece;
		squareToLabel(piece.getP()).highLight();
		for (Square i : chess.getAllSquares())
			if (!i.occupiedBy(chess.getWhoseTurn()))
				if (chosen.canMove(i) || chosen.canCapture(i)) {
					squareToLabel(i).highLight();
				}
	}

	private SquareLabel squareToLabel(Square sqr) {
		return view.labelAt(sqr.X(), sqr.Y());
	}

	private Square labelToSquare(SquareLabel sql) {
		return chess.spotAt(sql.X(), sql.Y());
	}

	/**
	 * dehighlight the whole board
	 */
	public void deHighLightWholeBoard() {
		chosen = null;
		for (SquareLabel i : view.getAllLabels())
			if (i.isHighLight())
				i.deHighLight();
	}

	// private String canClaimDraw = "";
	// private Chess.DrawRequest r;
	//
	// /**
	// * Find out if it is legal to claim draw. If it is, ends the game and
	// claim
	// * draw, otherwise send a request for draw, and wait for the reply of
	// * opponent.
	// *
	// * @return
	// */
	// public String askForDraw() {
	// if (canClaimDraw.isEmpty()) {
	// if (r.canAskFordraw(chess.getWhoseTurn())) {
	// while (true) {
	// String command = JOptionPane.showInputDialog("Do you agree draw?");
	// if (command.isEmpty())
	// continue;
	// if (command.toLowerCase().startsWith("yes")) {
	// return chess.draw("Draw by Agreement.");
	// } else if (command.toLowerCase().startsWith("no"))
	// break;
	// }
	// r.setRightToRequestDraw(chess.getWhoseTurn());
	// return "Request declined";
	// } else {
	// return "You cannot request for draw again now.";
	// }
	//
	// } else {
	// return chess.draw(canClaimDraw);
	// }
	//
	// }

}