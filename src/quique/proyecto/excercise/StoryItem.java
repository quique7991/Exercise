package quique.proyecto.excercise;

public class StoryItem {
	public static enum ActionType{Jump,Photo};
	public static class Action{
		public ActionType Tipo;
		public int Cantidad;
		
		public Action(ActionType type, int Cant){
			Cantidad = Cant;
			Tipo = type;
		}
	}
	public String Mensaje;
	public String Success;
	public String Fail;
	public int SuccDest;
	public int FailDest;
	public Action action;

	
	public StoryItem(String Mens, String Succ
			,String Fa, int SuccDestiny, int FailDestiny,
			Action action_arg){
		Mensaje = Mens;
		Success = Succ;
		Fail = Fa;
		SuccDest = SuccDestiny;
		FailDest = FailDestiny;
		action = action_arg;
	}
}