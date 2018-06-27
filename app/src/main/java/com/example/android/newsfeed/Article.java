package com.example.android.newsfeed;

/**
 * {@link Article} represents information for a single news article.
 * Each object has 4 properties: title, section, publish date and URL.
 */
class Article {

    //region Fields and Properties

    // Title of the article
    private final String mTitle;
    public String getTitle() {
        return mTitle;
    }

    // Section of the article
    private final String mSectionName;
    public String getSectionName() {
        return mSectionName;
    }

    // Author of the article
    private final String mAuthor;
    public String getAuthor() {
        return mAuthor;
    }

    // Publish date of the article
    private final String mPublishDate;
    public String getPublishDate() {
        return mPublishDate;
    }

    // Url to article content
    private final String mUrl;
    public String getUrl() {
        return mUrl;
    }

    //endregion

    /**
     * Constructs a new {@link Article} object.
     *
     * @param title is the title of the article
     * @param section is the section categorization of the article
     * @param author is the author date of the article
     * @param publishDate is the date of the article
     * @param url is the URL for the article's Guardian web page
     */
    public Article(String title, String section, String author, String publishDate, String url)
    {
        mTitle = title;
        mSectionName = section;
        mAuthor = author;
        mPublishDate = publishDate;
        mUrl = url;
    }
 }
