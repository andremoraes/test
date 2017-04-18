/* Generated from Java with JSweet 1.2.0 - http://www.jsweet.org */
namespace controller {
    import Move = model.Move;

    import Player = model.Piece.Player;

    import ChessViewer = view.ChessViewer;

    /**
     * The chess controller opens a single chess view
     * 
     * @author zhang
     */
    export class SingleViewChessControl extends ViewController {
        private view : ChessViewer;

        /**
         * start my little chess game!!!!
         * 
         * @param args
         * ignored
         */
        public constructor() {
            super();
            this.view = new ChessViewer(this, "The Great Chess Game", true);
            this.updateChessBoard();
        }

        public chooesView(whiteOrBlack : boolean) : ChessViewer {
            return this.view;
        }

        updateGuiAfterMove(previousMove : Move) {
            this.updateChessBoard();
            this.view.setStatusLabelText(this.chess.lastMoveDiscript());
            this.view.cleanTemp();
            this.view.printOut(this.chess.lastMoveOutPrint());
            this.view.printOut("Next move -- " + ViewController.side(previousMove.getWhoseTurn() === Player.BLACK));
        }

        public static main(args : string[]) {
            new SingleViewChessControl();
        }
    }
    SingleViewChessControl["__class"] = "controller.SingleViewChessControl";
    SingleViewChessControl["__interfaces"] = ["view.IChessViewerControl"];


}


controller.SingleViewChessControl.main(null);
