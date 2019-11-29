package com.qeeka.util;

import com.qeeka.domain.QueryCombineNode;
import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryHandle;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryOperateNode;
import com.qeeka.domain.Sort;
import com.qeeka.enums.Direction;
import com.qeeka.query.Criteria;
import com.qeeka.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by neal.xu on 2019/07/31.
 */
public class QueryParserHandle {

    public static QueryModel parse(Query query) {
        if (query == null) {
            return new QueryModel();
        }
        if (query.getCriteria() == null) {
            return parse(null, query.getSort());
        }
        return parse(query.getCriteria().getCriteriaChain(), query.getSort());
    }

    public static QueryModel parse(Criteria criteria) {
        if (criteria == null) {
            return new QueryModel();
        }
        return parse(criteria.getCriteriaChain(), null);
    }

    public static QueryModel parse(QueryGroup queryGroup) {
        if (queryGroup == null) {
            return new QueryModel();
        }
        return parse(queryGroup.getQueryHandleList(), queryGroup.getSort());
    }

    public static QueryModel parse(List<QueryHandle> queryHandleList, Sort sort) {
        QueryModel queryModel = new QueryModel();
        //Set order statement
        String orderStatement = generateOrderStatement(sort);
        queryModel.setOrderStatement(orderStatement);


        if (queryHandleList == null || queryHandleList.isEmpty()) {
            return queryModel;
        }

        //Handle alone node
        if (queryHandleList.size() == 1) {
            queryModel.setConditionStatement(generateParameterHql(queryHandleList.get(0), queryModel.getParameters()).toString());
            return queryModel;
        }

        //Handle multi node
        Stack<QueryHandle> handleStack = new Stack<>();
        for (int index = 0; index < queryHandleList.size(); index++) {
            QueryHandle currentNode = queryHandleList.get(index);
            if (currentNode instanceof QueryOperateNode) {
                QueryOperateNode operateNode = (QueryOperateNode) currentNode;

                QueryHandle node1 = handleStack.pop();
                QueryHandle node2 = handleStack.pop();

                //when express like a b +
                StringBuilder combineSql = new StringBuilder(32).append('(')
                        .append(generateParameterHql(node2, queryModel.getParameters()))
                        .append(operateNode.getQueryLinkOperate().getValue())
                        .append(generateParameterHql(node1, queryModel.getParameters()))
                        .append(')');
                handleStack.push(new QueryCombineNode(combineSql.toString()));
            } else {
                handleStack.add(currentNode);
            }
        }
        QueryCombineNode handle = (QueryCombineNode) handleStack.pop();
        queryModel.setConditionStatement(handle.getValue());
        return queryModel;
    }

    private static String generateOrderStatement(Sort sort) {
        if (sort == null) {
            return null;
        }

        StringBuilder orderStatement = new StringBuilder();
        for (Sort.Order order : sort) {
            if (order.getDirection().equals(Direction.ASC_NULL) || order.getDirection().equals(Direction.DESC_NULL)) {
                orderStatement.append("ISNULL(").append(order.getProperty()).append(") ").append(order.getDirection().getValue()).append(',');
            } else if (order.getDirection().equals(Direction.ASC_FIELD)) {
                orderStatement.append("FIELD(").append(order.getProperty()).append(") ASC,");
            } else if (order.getDirection().equals(Direction.DESC_FIELD)) {
                orderStatement.append("FIELD(").append(order.getProperty()).append(") DESC,");
            } else {
                orderStatement.append(order.getProperty()).append(' ').append(order.getDirection().getValue()).append(',');
            }
        }
        if (orderStatement.length() > 0) {
            orderStatement.setLength(orderStatement.length() - 1);
        }
        return orderStatement.toString();
    }

    public static CharSequence generateParameterHql(QueryHandle handle, Map<String, Object> parameters) {
        if (handle instanceof QueryNode) {
            QueryNode node = (QueryNode) handle;

            StringBuilder queryPart = new StringBuilder(node.getColumnName()).append(node.getQueryOperate().getValue());
            StringBuilder parameterName = new StringBuilder(node.getColumnName().replace(".", "_")).append(parameters.size());

            switch (node.getQueryOperate()) {
                case IS_NULL:
                    queryPart.append(" IS NULL");
                    parameterName = null;
                    break;
                case IS_NOT_NULL:
                    queryPart.append(" IS NOT NULL");
                    parameterName = null;
                    break;
                case COLUMN_EQUALS:
                    queryPart.append(node.getValue());
                    parameterName = null;
                    break;
                case COLUMN_NO_EQUALS:
                    queryPart.append(node.getValue());
                    parameterName = null;
                    break;
                case IN:
                case NOT_IN:
                    queryPart = new StringBuilder(node.getColumnName()).append(node.getQueryOperate().getValue()).append("(:").append(parameterName).append(')');
                    break;
                case CONTAIN:
                case NOT_CONTAIN:
                    queryPart.append(':').append(parameterName);
                    node.setValue(String.format("%%%s%%", node.getValue()));
                    break;
                case SUB_QUERY:
                    if (node.getValue() instanceof Map) {
                        Map<String, Object> parameter = (Map<String, Object>) node.getValue();
                        parameters.putAll(parameter);
                    }
                    parameterName = null;
                    break;
                default:
                    queryPart.append(':').append(parameterName);
            }

            if (parameterName != null) {
                parameters.put(parameterName.toString(), node.getValue());
            }
            return queryPart;
        }
        if (handle instanceof QueryCombineNode) {
            return ((QueryCombineNode) handle).getValue();
        }
        return "";
    }
}
