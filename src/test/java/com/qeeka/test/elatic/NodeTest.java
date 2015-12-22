package com.qeeka.test.elatic;

import com.qeeka.domain.elastic.ESSearchGroup;
import com.qeeka.domain.elastic.custom.ESAggsTermsNode;
import com.qeeka.domain.elastic.custom.ESBoolGroup;
import com.qeeka.domain.elastic.custom.ESExistsNode;
import com.qeeka.domain.elastic.custom.ESMissingNode;
import com.qeeka.domain.elastic.custom.ESRangeNode;
import com.qeeka.domain.elastic.custom.ESTermNode;
import com.qeeka.domain.elastic.custom.ESTermsNode;
import com.qeeka.domain.elastic.custom.ESWildcardNode;
import com.qeeka.domain.elastic.node.ESAggregationNode;
import com.qeeka.domain.elastic.node.ESQueryNode;
import com.qeeka.operate.Direction;
import com.qeeka.util.QueryJSONBinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by neal.xu on 2015/10/20
 */
public class NodeTest {
    @Test
    public void testBoolNode() {

        ESBoolGroup boolNode = new ESBoolGroup();
        boolNode.addMust(new ESTermNode("status", "pass"));
        boolNode.addMust(new ESTermNode("id", 315));

        boolNode.addMustNot(new ESTermNode("id", 1));

        String script = QueryJSONBinder.binder(ESBoolGroup.class).toJSON(boolNode);
        Assert.assertEquals(script, "{\"bool\":{\"must\":[{\"term\":{\"status\":\"pass\"}},{\"term\":{\"id\":315}}],\"must_not\":[{\"term\":{\"id\":1}}]}}");
    }

    @Test
    public void testSearchGroup() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        ESQueryNode queryNode = searchGroup.generateQueryNode();

        queryNode.addMust(new ESTermNode("status", "pass"));
        queryNode.addMust(new ESTermNode("id", 315));
        queryNode.addMustNot(new ESTermNode("id", 12));
        queryNode.addMust(new ESWildcardNode("nickName", "*8216*"));

        queryNode.addShould(new ESTermNode("id", 315));
        queryNode.addShould(new ESTermNode("id", 11));
        queryNode.addShould(new ESTermNode("id", 12));

        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{\"query\":{\"bool\":{\"must\":[{\"term\":{\"status\":\"pass\"}},{\"term\":{\"id\":315}},{\"wildcard\":{\"nickName\":\"*8216*\"}}],"
                + "\"should\":[{\"term\":{\"id\":315}},{\"term\":{\"id\":11}},{\"term\":{\"id\":12}}],\"must_not\":[{\"term\":{\"id\":12}}]}}}}}");
    }

    @Test
    public void tetBoolInBool() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        ESQueryNode queryNode = searchGroup.generateQueryNode();

        queryNode.addMust(new ESTermNode("id", 315));

        ESBoolGroup internalBool = new ESBoolGroup();
        internalBool.addMust(new ESTermNode("status", "pass"));
        queryNode.addMust(internalBool);

        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{\"query\":{\"bool\":{\"must\":[{\"term\":{\"id\":315}},{\"bool\":{\"must\":[{\"term\":{\"status\":\"pass\"}}]}}]}}}}}");
    }

    @Test
    public void testFilter() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.generateFilterNode().addMust(new ESTermNode("status", "organization"));
        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{\"filter\":{\"bool\":{\"must\":[{\"term\":{\"status\":\"organization\"}}]}}}}}");
    }

    @Test
    public void testSort() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.addSort("date", Direction.DESC);
        searchGroup.addSort("_score", Direction.ASC);
        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{}},\"sort\":[{\"date\":{\"order\":\"desc\"}},{\"_score\":{\"order\":\"asc\"}}]}");
    }

    @Test
    public void testPageSize() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.setFrom(0);
        searchGroup.setSize(10);
        Assert.assertEquals(searchGroup.generateScript(), "{\"from\":0,\"size\":10,\"query\":{\"filtered\":{}}}");
    }

    @Test
    public void testMissNode() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.generateFilterNode().addMust(new ESMissingNode("designateDesignerId"))
                .addMust(new ESExistsNode("userPhone"));
        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{\"filter\":{\"bool\":{\"must\":[{\"missing\":{\"field\":\"designateDesignerId\"}},{\"exists\":{\"field\":\"userPhone\"}}]}}}}}");
    }

    @Test
    public void complexTest() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.setFrom(0).setSize(10).addSort("updateTime", Direction.DESC);

        searchGroup.generateQueryNode().addMust(new ESWildcardNode("allotOrganizationName", "*simon李*"))
                .addMust(new ESWildcardNode("designerPhone", "*132*"));

        searchGroup.generateFilterNode().addMust(new ESTermNode("status", "organization"));

        Assert.assertEquals(searchGroup.generateScript(), "{\"from\":0,\"size\":10,\"query\":{\"filtered\":{\"query\":{\"bool\":{\"must\":[{\"wildcard\":{\"allotOrganizationName\":\"*simon李*\"}},{\"wildcard\":{\"designerPhone\":\"*132*\"}}]}},\"filter\":{\"bool\":{\"must\":[{\"term\":{\"status\":\"organization\"}}]}}}},\"sort\":[{\"updateTime\":{\"order\":\"desc\"}}]}");
    }

    @Test
    public void testEmptySearch() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.setFrom(0).setSize(10);
        searchGroup.generateQueryNode();
        searchGroup.generateFilterNode();
        Assert.assertEquals(searchGroup.generateScript(), "{\"from\":0,\"size\":10,\"query\":{\"filtered\":{}}}");

        ESSearchGroup searchGroup2 = new ESSearchGroup();
        searchGroup2.generateQueryNode();
        searchGroup2.generateQueryNode();
        searchGroup2.setFrom(0).setSize(10).addSort("updateTime", Direction.DESC);
        Assert.assertEquals(searchGroup2.generateScript(), "{\"from\":0,\"size\":10,\"query\":{\"filtered\":{}},\"sort\":[{\"updateTime\":{\"order\":\"desc\"}}]}");
    }

    @Test
    public void testRange() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.generateFilterNode().addMust(new ESRangeNode("updateTime", "2015-01-23 16:51:50", false, "2015-01-23 16:51:50||+1M", false));
        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{\"filter\":{\"bool\":{\"must\":[{\"range\":{\"updateTime\":{\"gt\":\"2015-01-23 16:51:50\",\"lt\":\"2015-01-23 16:51:50||+1M\"}}}]}}}}}");
    }

    @Test
    public void testGroupBy() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.setFrom(0).setSize(0);

        searchGroup.generateQueryNode().addMust(new ESWildcardNode("status", "*pass*"));
        searchGroup.generateFilterNode().addMust(new ESTermsNode("designateDesignerId", Arrays.asList(101526953, 101528895)));


        ESAggregationNode aggregationNode = new ESAggsTermsNode("designateDesignerId")
                .addAggregations("status", new ESAggsTermsNode("status")
                        .addAggregations("user_id", new ESAggsTermsNode("userId")));

        searchGroup.addAggregations("designate_designer_id", aggregationNode);
        Assert.assertEquals(searchGroup.generateScript(), "{\"from\":0,\"size\":0,\"query\":{\"filtered\":{\"query\":{\"bool\":{\"must\":[{\"wildcard\":{\"status\":\"*pass*\"}}]}},\"filter\":{\"bool\":{\"must\":[{\"terms\":{\"designateDesignerId\":[101526953,101528895]}}]}}}},\"aggs\":{\"designate_designer_id\":{\"aggs\":{\"status\":{\"aggs\":{\"user_id\":{\"terms\":{\"field\":\"userId\"}}},\"terms\":{\"field\":\"status\"}}},\"terms\":{\"field\":\"designateDesignerId\"}}}}");
    }

    @Test
    public void testHighlight() {
        ESSearchGroup searchGroup = new ESSearchGroup();
        searchGroup.generateHighlightNode().addHighlightFields("title", "labels").addHighlightPreTag("<font color=\"#777755\">").addHighlightPostTag("</font>");
        Assert.assertEquals(searchGroup.generateScript(), "{\"query\":{\"filtered\":{}},\"highlight\":{\"pre_tags\":[\"<font color=\\\"#777755\\\">\"],\"post_tags\":[\"</font>\"],\"fields\":{\"title\":{},\"labels\":{}}}}");
    }
}
