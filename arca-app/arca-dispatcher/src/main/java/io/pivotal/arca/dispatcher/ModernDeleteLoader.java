/* 
 * Copyright (C) 2014 Pivotal Software, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.arca.dispatcher;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ModernDeleteLoader extends ModernResultLoader<DeleteResult> {

	public ModernDeleteLoader(final Context context, final RequestExecutor executor, final Delete request) {
		super(context, executor, request);
	}

	@Override
	public DeleteResult loadInBackground() {
		final Delete delete = (Delete) getContentRequest();
		final RequestExecutor executor = getRequestExecutor();
		return executor.execute(delete);
	}

	@Override
	public DeleteResult getErrorResult(final Error error) {
		return new DeleteResult(error);
	}

}
