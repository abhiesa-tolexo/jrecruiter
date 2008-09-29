package org.jrecruiter.web.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.displaytag.properties.SortOrderEnum;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeFactory;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jrecruiter.common.CollectionUtils;
import org.jrecruiter.model.Job;
import org.jrecruiter.model.util.FilteringUtils.JobFilterableProperty;
import org.jrecruiter.model.util.FilteringUtils.SortOrder;
import org.jrecruiter.web.DisplaytagPaginatedList;
import org.jrecruiter.web.interceptor.RetrieveMessages;
import org.springframework.web.HttpRequestHandler;
import org.texturemedia.smarturls.ActionName;
import org.texturemedia.smarturls.ActionNames;
import org.texturemedia.smarturls.Result;
import org.texturemedia.smarturls.Results;

/**
 * Show a list of jobs.
 *
 * @author Gunnar Hillert
 * @version $Id:UserService.java 128 2007-07-27 03:55:54Z ghillert $
 */
@Results(
        {
            @Result(name="ajax",    location="/WEB-INF/jsp/jobsTable.jsp"),
            @Result(name="success", location="/WEB-INF/jsp/show-jobs.jsp")
        }
    )
@ActionNames({
  @ActionName(name="show-jobs"),
  @ActionName(name="show-jobs-ajax")
})
public class ShowJobsAction extends BaseAction implements ServletRequestAware {


    /** serialVersionUID. */
    private static final long serialVersionUID = 369806210598096582L;

    /**
     * Logger Declaration.
     */
    private final Log LOGGER = LogFactory.getLog(ShowJobsAction.class);

    private HttpServletRequest request;

    private String ajax;
    private List<Job> jobs           = CollectionUtils.getArrayList();
    private Limit limit;

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @SuppressWarnings("unchecked")
    @RetrieveMessages
    @Override
    public String execute() throws Exception {

        final TableFacade tableFacade = TableFacadeFactory.createTableFacade("jobsTable", request);

        limit                     = tableFacade.getLimit();
        final FilterSet filterSet = limit.getFilterSet();
        final SortSet sortSet     = limit.getSortSet();


        final int totalRows = jobService.getJobsCount().intValue();
        tableFacade.setTotalRows(totalRows);

        int page     = limit.getRowSelect().getPage();
        int maxRows  = limit.getRowSelect().getMaxRows();

        final Map<String, String> sortOrders              = CollectionUtils.getHashMap();
        final Map<String, String> jobFilters = CollectionUtils.getHashMap();

        if (sortSet.isSorted()) {
            for (Sort sort : sortSet.getSorts()) {
                sortOrders.put(sort.getProperty(), sort.getOrder().name());
            }
        }

        if (sortOrders.isEmpty()) {
            sortOrders.put("updateDate", "DESC");
        }

        if (filterSet.isFiltered()) {
            for (Filter filter : filterSet.getFilters()) {
                jobFilters.put(filter.getProperty(), filter.getValue());
            }
        }

        LOGGER.info("Retrieving all jobs - "
                        + ";Total Size: " + totalRows
                        + ";Results per Page: " + maxRows
                        + ";Page: " + page);

        this.jobs = jobService.getJobs(maxRows, page, sortOrders, jobFilters);

        if (ajax != null && ajax.equalsIgnoreCase("true")) {
            return "ajax";
        }

        return SUCCESS;
    }

    //~~~~~Getters and Setters~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public String getAjax() {
        return ajax;
    }

    public void setAjax(String ajax) {
        this.ajax = ajax;
    }

}