/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.ItemContract.itemEntry;

import static com.example.android.pets.data.ItemProvider.LOG_TAG;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of item data as its data source. This adapter knows
 * how to create list items for each row of item data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    private TextView mQuantityTextView;

    private String mQuantityString;

    private int mRowsAffected;

    private Context mContext;

    private int mRowId;

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    //public ItemCursorAdapter(Context context,Cursor cursor){
    //    super(context,cursor,0);
    //}

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView costTextView = (TextView) view.findViewById(R.id.cost);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        mQuantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(itemEntry.COLUMN_ITEM_NAME);
        int costColumnIndex = cursor.getColumnIndex(itemEntry.COLUMN_ITEM_COST);
        int quantityColumnIndex = cursor.getColumnIndex(itemEntry.COLUMN_ITEM_QUANTITY);

        // Get the ID for the item row
        mRowId = cursor.getInt(cursor.getColumnIndex(itemEntry._ID));
        // Context variable for the context passed in
        mContext = context;

        final int position = cursor.getPosition();

        // Click button for "Item Sold" on detail layout, update quantity and sold numbers
        Button saleButton = (Button) view.findViewById(R.id.sale_Button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                saleItem();
            }
        });

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemCost = cursor.getString(costColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);

        // If the item column is empty string or null, then use some default text
        // so the TextView isn't blank.
        if (TextUtils.isEmpty(itemName)) {
            itemName = context.getString(R.string.unknown_name);
        }

        if (TextUtils.isEmpty(itemCost)) {
            itemCost = context.getString(R.string.unknown_cost);
        }

        if (TextUtils.isEmpty(itemQuantity)) {
            itemQuantity = context.getString(R.string.unknown_quantity);
        }

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        costTextView.setText(itemCost);
        quantityTextView.setText(itemQuantity);
    }

    // Executed when "Sale" button is pressed, takes away from quantity
    public int saleItem() {
        int quantity = Integer.parseInt(mQuantityTextView.getText().toString());

        if (quantity > 0) {
            quantity--;

            mQuantityString = Integer.toString(quantity);

            ContentValues values = new ContentValues();
            values.put(itemEntry.COLUMN_ITEM_QUANTITY, mQuantityString);

            Uri currentProductUri = ContentUris.withAppendedId(itemEntry.CONTENT_URI,
                    mRowId);

            Log.e(LOG_TAG, "currentProductUri: " + String.valueOf(currentProductUri));

            mRowsAffected = mContext.getContentResolver().update(currentProductUri, values,
                    null, null);
        }
        return mRowsAffected;
    }

}

