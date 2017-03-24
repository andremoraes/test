package model;

/**
 * 
 * @author zhangq2
 *
 */
public class King extends Piece {
	private final int VALUE = 100;

	/**
	 * constructs a King with initial square
	 * 
	 * @param c
	 * @param Position
	 * @param chess
	 */
	public King(Player c, Square Position, Chess chess) {
		super(c, Position, chess);
	}

	@Override
	public Move legalPosition(Square end) {
		if (Math.abs(spot.X() - end.X()) > 1 || Math.abs(spot.Y() - end.Y()) > 1)
			return null;
		if (spot.equals(end))
			return null;
		else {
			return new RegularMove(this, spot, end.getPiece(), end);
		}
	}

	@Override
	protected Move getMove(Square end) {
		if (end.occupiedBy(super.color)) 
			return null;

		Move legalMove = legalPosition(end);
		if (legalMove == null) {
			if (getX() == 5 && getY() == end.Y()) {
				if (end.X() == 3) {
					return chess.canCastling(this, true);
				} else if (end.X() == 7) {
					return chess.canCastling(this, false);
				}
			}
			return null;
		}

		if (chess.giveAwayKing(legalMove))
			return null;
		return legalMove;
	}

	@Override
	public int getValue() {
		return VALUE;
	}

	@Override
	public char getType() {
		return 'K';
	}
}
