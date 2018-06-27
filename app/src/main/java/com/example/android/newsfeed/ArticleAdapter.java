package com.example.android.newsfeed;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class ArticleAdapter extends ArrayAdapter<Article> {

    private final String INPUT_PUBLISHED_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final String OUTPUT_PUBLISHED_DATE_FORMAT_STRING = "LLL dd, yyyy";

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param articles A List of {@link Article} objects to display in a list
     */
    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);

            // Find and persist the views in the article_list_item.xml
            viewHolder = new ViewHolder();
            viewHolder.titleView = convertView.findViewById(R.id.article_title);
            viewHolder.authorView = convertView.findViewById(R.id.article_author);
            viewHolder.sectionNameView = convertView.findViewById(R.id.article_section);
            viewHolder.publishDateView = convertView.findViewById(R.id.publication_date);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the {@link Article} object located at this position in the list
        Article currentArticle = getItem(position);

        // Set the title TextView value
        viewHolder.titleView.setText(currentArticle.getTitle());

        // Set the section name TextView value
        viewHolder.authorView.setText(currentArticle.getAuthor());

        // Set the section name TextView value
        viewHolder.sectionNameView.setText(currentArticle.getSectionName());

        // Set the publish date TextView value
        viewHolder.publishDateView.setText(formatPublicationDate(currentArticle.getPublishDate()));

        // Return the whole list item layout so that it can be shown in the ListView
        return convertView;
    }

    /**
     * Reformats the given publication date string to one used in presentation
     *
     * @param strDate Input string to be reformatted
     * @return the given date string in desired presentation format or empty
     * string if unable to parse
     */
    private String formatPublicationDate(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(INPUT_PUBLISHED_DATE_FORMAT_STRING, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date dateObject = simpleDateFormat.parse(strDate);
            simpleDateFormat = new SimpleDateFormat(OUTPUT_PUBLISHED_DATE_FORMAT_STRING, Locale.US);
            return simpleDateFormat.format(dateObject);
        } catch (ParseException e) {
            // Cant parse, just return empty string
            return "";
        }
    }

    /**
     *  Nested class that provides implementation of the view holder pattern.
     *  It is used to persist the list_item resource identifiers so that they are only
     *  found {@link View#findViewById} once.
     */
    static class ViewHolder {
        private TextView titleView;
        private TextView sectionNameView;
        private TextView authorView;
        private TextView publishDateView;
    }
}

