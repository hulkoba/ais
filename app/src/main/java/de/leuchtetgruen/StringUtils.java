package de.leuchtetgruen;

import android.content.Context;

public class StringUtils {


	public static String localizedParameterizedString(Context ctx, int id, Object...params) {
		String format = ctx.getResources().getString(id);
		return String.format(format, params);
	}
	
}
