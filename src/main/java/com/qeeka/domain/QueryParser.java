package com.qeeka.domain;

import com.qeeka.operate.Direction;
import com.qeeka.operate.Sort;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by neal.xu on 7/31 0031.
 */
public class QueryParser {

    public QueryModel parse(QueryGroup queryGroup) {
        QueryGroup group = deepQueryGroupCopy(queryGroup);
        QueryModel queryModel = new QueryModel();
        if (group == null) {
            return queryModel;
        }
        List<QueryHandle> nodeStack = group.getQueryHandleList();
        //Set order statement
        String orderStatement = generateOrderStatement(group.getSort());
        queryModel.setOrderStatement(orderStatement);


        if (nodeStack == null || nodeStack.isEmpty()) {
            return queryModel;
        }

        //Handle alone node
        if (nodeStack.size() == 1) {
            queryModel.setStatement(generateParameterHql(nodeStack.get(0), queryModel.getParameters()));
            return queryModel;
        }

        Stack<CharSequence> hqlParts = new Stack<>();

        QueryHandle node1 = null;
        QueryHandle node2 = null;

        int lastScanIndex = 0;

        int index = 0;
        while (!nodeStack.isEmpty()) {
            QueryHandle currentNode = nodeStack.get(index);
            if (currentNode instanceof QueryOperateNode) {
                QueryOperateNode operateNode = (QueryOperateNode) currentNode;

                if (index - 2 >= 0) {
                    node2 = nodeStack.get(index - 2);
                }
                if (index - 1 >= 0) {
                    node1 = nodeStack.get(index - 1);
                }

                //when express like a b +
                if (lastScanIndex <= index - 2 && node1 instanceof QueryNode && node2 instanceof QueryNode) {
                    hqlParts.push(new StringBuilder().append('(')
                            .append(generateParameterHql(node2, queryModel.getParameters())).append(operateNode.getQueryLinkOperate().getValue())
                            .append(generateParameterHql(node1, queryModel.getParameters()))
                            .append(')'));
                    //remove handle node
                    nodeStack.remove(index--);
                    nodeStack.remove(index--);
                    nodeStack.remove(index);
                    lastScanIndex = index;
                } else {
                    CharSequence popNode = hqlParts.pop();

                    if (!hqlParts.isEmpty()) {
                        CharSequence popNode2 = hqlParts.pop();
                        hqlParts.push(
                                new StringBuilder().append('(').append(popNode2)
                                        .append(operateNode.getQueryLinkOperate().getValue())
                                        .append(popNode).append(')')
                        );
                        //remove operate node
                        nodeStack.remove(index--);
                    } else {
                        hqlParts.push(
                                new StringBuilder('(').append('(').append(generateParameterHql(node1, queryModel.getParameters()))
                                        .append(operateNode.getQueryLinkOperate().getValue())
                                        .append(popNode).append(')').append(')')
                        );
                        //remove operate node and query node
                        nodeStack.remove(index--);
                        nodeStack.remove(index);
                    }
                }
            } else {
                index++;
            }
        }
        queryModel.setStatement(hqlParts.pop().toString());
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
            } else {
                orderStatement.append(order.getProperty()).append(' ').append(order.getDirection().getValue()).append(',');
            }
        }
        if (orderStatement.length() > 0) {
            orderStatement.setLength(orderStatement.length() - 1);
        }
        return orderStatement.toString();
    }

    private static String generateParameterHql(QueryHandle handle, Map<String, Object> parameters) {
        if (handle instanceof QueryNode) {
            QueryNode node = (QueryNode) handle;

            StringBuilder queryPart = new StringBuilder(node.getColumnName()).append(node.getQueryOperate().getValue());
            StringBuilder parameterName = new StringBuilder(node.getColumnName()).append(parameters.size());

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
                    queryPart.append(":").append(parameterName);
                    node.setValue(String.format("%%%s%%", node.getValue()));
                    break;
                default:
                    queryPart.append(":").append(parameterName);
            }

            if (parameterName != null) {
                parameters.put(parameterName.toString(), node.getValue());
            }
            return queryPart.toString();
        }
        return "";
    }

    private QueryGroup deepQueryGroupCopy(QueryGroup queryGroup) {
        QueryGroup group = new QueryGroup();
        //Copy Node
        if (queryGroup != null) {
            if (queryGroup.getQueryHandleList() != null) {
                for (QueryHandle handle : queryGroup.getQueryHandleList()) {
                    if (handle instanceof QueryOperateNode) {
                        QueryOperateNode operateNode = new QueryOperateNode(((QueryOperateNode) handle).getQueryLinkOperate());
                        group.getQueryHandleList().add(operateNode);
                    } else if (handle instanceof QueryNode) {
                        QueryNode currentNode = (QueryNode) handle;
                        QueryNode queryNode = new QueryNode(currentNode.getColumnName(), currentNode.getValue(), currentNode.getQueryOperate());
                        group.getQueryHandleList().add(queryNode);
                    }
                }
            }
            group.setSort(queryGroup.getSort());
        }
        return group;
    }
}
