package quique.proyecto.excercise;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;

public class StoryReader  {
	public JsonReader reader;
	
	public StoryReader(InputStream InS) throws IOException{
		reader = new JsonReader(new InputStreamReader(InS,"UTF-8"));
	}
	
	public List<StoryItem> GetStory() throws IOException{
		List<StoryItem> storyItems = new ArrayList<StoryItem>();
		
		reader.beginArray();
		while(reader.hasNext()){
			storyItems.add(getStoryItem());
		}
		reader.endArray();
		return storyItems;
	}
	
	public StoryItem getStoryItem() throws IOException{
 		String Message = null,Success = null,Fail = null;
		int SuccDest = -1,FailDest = -1;
		StoryItem.Action action = null;
		
		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals("Message")){
				Message = reader.nextString();
			} else if(name.equals("Action")){
				action = readAction(reader);
			} else if(name.equals("Success")){
				Success = reader.nextString();
			} else if(name.equals("Fail")){
				Fail = reader.nextString();
			} else if(name.equals("SuccDest")){
				SuccDest = reader.nextInt();
			} else if(name.equals("FailDest")){
				FailDest = reader.nextInt();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new StoryItem(Message,Success,Fail,SuccDest,FailDest,action);
	}
	
	public StoryItem.Action readAction(JsonReader reader) throws IOException{
		int Cantidad = 0;
		StoryItem.ActionType type = null;
		
		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals("Type")){
				String typeAct = reader.nextString();
				if(typeAct.equals("Jump")){
					type = StoryItem.ActionType.Jump;
				} else if(typeAct.equals("Photo")){
					type = StoryItem.ActionType.Photo;
				} else if(typeAct.equals("North")){
					type = StoryItem.ActionType.North;
				} else if(typeAct.equals("Focus")){
					type = StoryItem.ActionType.Focus;
				}
			} else if(name.equals("Quantity")){
				Cantidad = reader.nextInt();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new StoryItem.Action(type,Cantidad);
	}
	
}
