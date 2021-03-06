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
package io.pivotal.arca.provider.test;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.pivotal.arca.provider.Column;
import io.pivotal.arca.provider.DatabaseConfiguration;
import io.pivotal.arca.provider.DatabaseConfiguration.DefaultDatabaseConfiguration;
import io.pivotal.arca.provider.DatabaseHelper;
import io.pivotal.arca.provider.SelectFrom;
import io.pivotal.arca.provider.SQLiteDataset;
import io.pivotal.arca.provider.SQLiteTable;
import io.pivotal.arca.provider.SQLiteView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatabaseHelperTest extends AndroidTestCase {

	public void testDatabaseHelperUpgradeDataset() {
		final AssertionLatch latch = new AssertionLatch(1);
		final ArrayList<SQLiteDataset> datasets = new ArrayList<SQLiteDataset>();
		datasets.add(new TestSQLiteTable() {

			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
				latch.countDown();

				assertEquals(0, oldVersion);
				assertEquals(1, newVersion);
			}

		});
		final DatabaseConfiguration config = new DefaultDatabaseConfiguration(getContext());
		final DatabaseHelper helper = DatabaseHelper.create(getContext(), config, datasets);
		helper.onUpgrade(null, 0, 1);
		latch.assertComplete();
	}

	public void testDatabaseHelperUpgradeMultipleDatasets() {
		final AssertionLatch latch = new AssertionLatch(2);
		final ArrayList<SQLiteDataset> datasets = new ArrayList<SQLiteDataset>();
		datasets.add(new TestSQLiteTable() {

			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
				latch.countDown();

				assertEquals(0, oldVersion);
				assertEquals(1, newVersion);
			}

		});
		datasets.add(new TestSQLiteView() {

			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
				latch.countDown();

				assertEquals(0, oldVersion);
				assertEquals(1, newVersion);
			}

		});
		final DatabaseConfiguration config = new DefaultDatabaseConfiguration(getContext());
		final DatabaseHelper helper = DatabaseHelper.create(getContext(), config, datasets);
		helper.onUpgrade(null, 0, 1);
		latch.assertComplete();
	}

	public void testDatabaseHelperDowngradeDataset() {
		final AssertionLatch latch = new AssertionLatch(1);
		final ArrayList<SQLiteDataset> datasets = new ArrayList<SQLiteDataset>();
		datasets.add(new TestSQLiteTable() {

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				latch.countDown();

				assertEquals(1, oldVersion);
				assertEquals(0, newVersion);
			}

		});
		final DatabaseConfiguration config = new DefaultDatabaseConfiguration(getContext());
		final DatabaseHelper helper = DatabaseHelper.create(getContext(), config, datasets);
		helper.onDowngrade(null, 1, 0);
		latch.assertComplete();
	}

	public void testDatabaseHelperDowngradeMultipleDatasets() {
		final AssertionLatch latch = new AssertionLatch(2);
		final ArrayList<SQLiteDataset> datasets = new ArrayList<SQLiteDataset>();
		datasets.add(new TestSQLiteTable() {

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				latch.countDown();

				assertEquals(1, oldVersion);
				assertEquals(0, newVersion);
			}

		});
		datasets.add(new TestSQLiteView() {

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				latch.countDown();

				assertEquals(1, oldVersion);
				assertEquals(0, newVersion);
			}

		});
		final DatabaseConfiguration config = new DefaultDatabaseConfiguration(getContext());
		final DatabaseHelper helper = DatabaseHelper.create(getContext(), config, datasets);
		helper.onDowngrade(null, 1, 0);
		latch.assertComplete();
	}

	// =======================================

	public static class TestSQLiteTable extends SQLiteTable {

		public static interface Columns {
            @Column(Column.Type.TEXT)
            public static final String ID = "id";
		}
	}

	public static class TestSQLiteView extends SQLiteView {

        @SelectFrom("TestSQLiteTable")
		public static interface Columns {}

	}

	// =======================================

	public class AssertionLatch extends CountDownLatch {

		public AssertionLatch(final int count) {
			super(count);
		}

		@Override
		public void countDown() {
			final long count = getCount();
			if (count == 0) {
				Assert.fail("This latch has already finished.");
			} else {
				super.countDown();
			}
		}

		public void assertComplete() {
			try {
				Assert.assertTrue(await(0, TimeUnit.SECONDS));
			} catch (final InterruptedException e) {
				Assert.fail();
			}
		}
	}
}
