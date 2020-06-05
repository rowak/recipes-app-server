package io.github.rowak.recipesappserver.net;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.rowak.recipesappserver.models.Recipe;
import io.github.rowak.recipesappserver.sql.RecipesDB;

public class RequestHandler {
	private RecipesDB db = new RecipesDB();
	
	/*
	 * Process a request from the client into a response action.
	 */
	public Response getResponse(Request request) {
		switch (request.getType()) {
			case VERSION:
				JSONObject obj = new JSONObject();
				obj.put("data", Server.VERSION);
				// No client verification required
				return new Response(ResponseType.VERSION, obj);
			case CATEGORIES:
				return getCategoriesResponse(request);
			case RECIPE_HEADERS:
				return getRecipeHeadersResponse(request);
			case RECIPE:
				return getRecipeResponse(request);
			default:
				return Response.INVALID_REQUEST;
		}
	}
	
	private Response getCategoriesResponse(Request request) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	private Response getRecipeHeadersResponse(Request request) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	private Response getRecipeResponse(Request request) {
		JSONObject data = request.getData();
		String recipeName = data.has("recipeName") ?
				data.getString("recipeName") : null;
		if (recipeName == null) {
			return Response.INVALID_REQUEST;
		}
		
		try {
			db.connect();
			Recipe recipe = db.getRecipe(recipeName);
			for (io.github.rowak.recipesappserver.models.Ingredient ingredient : recipe.getIngredients()) {
				System.out.println(ingredient.toString());
			}
			db.disconnect();
			if (recipe != null) {
				return new Response(ResponseType.RECIPE,
						getDataObject(recipe.toJSON()));
			}
			return Response.RESOURCE_NOT_FOUND;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return Response.DATABASE_ERROR;
		}
	}
	
	/*
	 * Wraps a json array object in a json object under the name "data".
	 * Placed directly into the json of a response object.
	 */
	private JSONObject getDataObject(JSONArray arr) {
		JSONObject obj = new JSONObject();
		obj.put("data", arr);
		return obj;
	}
	
	/*
	 * Wraps a json object in another json object under the name "data".
	 * Placed directly into the json of a response object.
	 */
	private JSONObject getDataObject(JSONObject obj) {
		JSONObject objData = new JSONObject();
		objData.put("data", obj);
		return objData;
	}
	
	/*
	 * Wraps a integer value in a json object under the name "data".
	 * Placed directly into the json of a response object.
	 */
	private JSONObject getDataObject(int val) {
		JSONObject obj = new JSONObject();
		obj.put("data", val);
		return obj;
	}
	
	/*
	 * Wraps a boolean value in a json object under the name "data".
	 * Placed directly into the json of a response object.
	 */
	private JSONObject getDataObject(boolean val) {
		JSONObject obj = new JSONObject();
		obj.put("data", val);
		return obj;
	}
}
