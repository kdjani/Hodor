
package com.app.hodor;

import android.content.Context;


public class CreateUserServiceAPI extends ServiceAPIs  implements IHodorService { 
	
	public CreateUserServiceAPI (){
		super();
	}

	public CreateUserServiceAPI (Context a)  {
		super(a);
	}

    @Override
    protected void onPostExecute(String result) {             
        super.onPostExecute(result); 
        if (associatedActivity instanceof OnServiceResponse) {
        	OnServiceResponse response = (OnServiceResponse)(associatedActivity);
        	response.onSuccess(result);
        }
    } 
}
