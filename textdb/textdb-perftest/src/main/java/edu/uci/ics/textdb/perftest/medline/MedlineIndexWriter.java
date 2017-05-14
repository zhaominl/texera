package edu.uci.ics.textdb.perftest.medline;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.textdb.api.exception.StorageException;
import edu.uci.ics.textdb.api.field.IField;
import edu.uci.ics.textdb.api.schema.Attribute;
import edu.uci.ics.textdb.api.schema.AttributeType;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.tuple.Tuple;
import edu.uci.ics.textdb.storage.DataWriter;
import edu.uci.ics.textdb.storage.RelationManager;
import edu.uci.ics.textdb.storage.utils.StorageUtils;

/*
 * This class defines Medline data schema.
 * It also provides functions to read Medline file 
 * from local machine, parse json format,
 * and return a generated ITuple one by one.
 * 
 * @author Zuozhi Wang
 * @author Jinggang Diao
 */
public class MedlineIndexWriter {

    public static final String PMID = "pmid";
    public static final String AFFILIATION = "affiliation";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String AUTHORS = "authors";
    public static final String JOURNAL_ISSUE = "journal_issue";
    public static final String JOURNAL_TITLE = "journal_title";
    public static final String KEYWORDS = "keywords";
    public static final String MESH_HEADINGS = "mesh_headings";
    public static final String ABSTRACT = "abstract";
    public static final String ZIPF_SCORE = "zipf_score";

    public static final Attribute PMID_ATTR = new Attribute(PMID, AttributeType.INTEGER);
    public static final Attribute AFFILIATION_ATTR = new Attribute(AFFILIATION, AttributeType.TEXT);
    public static final Attribute ARTICLE_TITLE_ATTR = new Attribute(ARTICLE_TITLE, AttributeType.TEXT);
    public static final Attribute AUTHORS_ATTR = new Attribute(AUTHORS, AttributeType.TEXT);
    public static final Attribute JOURNAL_ISSUE_ATTR = new Attribute(JOURNAL_ISSUE, AttributeType.STRING);
    public static final Attribute JOURNAL_TITLE_ATTR = new Attribute(JOURNAL_TITLE, AttributeType.TEXT);
    public static final Attribute KEYWORDS_ATTR = new Attribute(KEYWORDS, AttributeType.TEXT);
    public static final Attribute MESH_HEADINGS_ATTR = new Attribute(MESH_HEADINGS, AttributeType.TEXT);
    public static final Attribute ABSTRACT_ATTR = new Attribute(ABSTRACT, AttributeType.TEXT);
    public static final Attribute ZIPF_SCORE_ATTR = new Attribute(ZIPF_SCORE, AttributeType.DOUBLE);

    public static final Attribute[] ATTRIBUTES_MEDLINE = { PMID_ATTR, AFFILIATION_ATTR, ARTICLE_TITLE_ATTR,
            AUTHORS_ATTR, JOURNAL_ISSUE_ATTR, JOURNAL_TITLE_ATTR, KEYWORDS_ATTR, MESH_HEADINGS_ATTR, ABSTRACT_ATTR,
            ZIPF_SCORE_ATTR };

    public static final Schema SCHEMA_MEDLINE = new Schema(ATTRIBUTES_MEDLINE);

    public static Tuple recordToTuple(String record) throws IOException, ParseException {
        JsonNode jsonNode = new ObjectMapper().readValue(record, JsonNode.class);
        ArrayList<IField> fieldList = new ArrayList<IField>();
        for (Attribute attr : ATTRIBUTES_MEDLINE) {
            fieldList.add(StorageUtils.getField(attr.getAttributeType(), jsonNode.get(attr.getAttributeName()).toString()));
        }
        IField[] fieldArray = new IField[fieldList.size()];
        Tuple tuple = new Tuple(SCHEMA_MEDLINE, fieldList.toArray(fieldArray));
        return tuple;
    }
    
    public static void writeMedlineIndex(String medlineFilepath, String tableName) throws IOException, StorageException, ParseException {
        RelationManager relationManager = RelationManager.getRelationManager();
        DataWriter dataWriter = relationManager.getTableDataWriter(tableName);
        dataWriter.open();
        
        BufferedReader reader = Files.newBufferedReader(Paths.get(medlineFilepath));
        String line;
        while ((line = reader.readLine()) != null) {
            dataWriter.insertTuple(recordToTuple(line));
        }
        reader.close();
        dataWriter.close(); 
    }

}
