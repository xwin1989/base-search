package com.qeeka.util;

import com.qeeka.domain.QueryCombineNode;
import com.qeeka.domain.QueryHandle;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryOperateNode;
import com.qeeka.enums.QueryOperate;
import com.qeeka.query.Criteria;
import com.qeeka.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by neal.xu on 2019/07/31.
 */
public class CriteriaParserHandle {

    public static QueryModel parse(Criteria criteria) {
        if (criteria == null) {
            return new QueryModel();
        }
        return parse(criteria.getCriteriaChain());
    }

    public static QueryModel parse(List<QueryHandle> queryHandleList) {
        QueryModel queryModel = new QueryModel();

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

    public static CharSequence generateParameterHql(QueryHandle handle, Map<String, Object> parameters) {
        if (handle instanceof QueryNode) {
            QueryNode node = (QueryNode) handle;

            if (node.getQueryOperate().equals(QueryOperate.SUB_QUERY)) {
                Map<String, Object> params = (Map) node.getValue();
                Object sql = params.get(Query.SPECIAL_QUERY_SQL_KEY);
                parameters.remove(Query.SPECIAL_QUERY_SQL_KEY);
                parameters.putAll(params);
                return sql.toString();
            }

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
