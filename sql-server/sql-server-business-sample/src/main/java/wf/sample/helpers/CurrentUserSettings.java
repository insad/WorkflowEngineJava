package wf.sample.helpers;

import business.helpers.EmployeeHelper;
import business.models.Employee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;
import optimajet.workflow.core.util.UUIDUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentUserSettings {

    private static final String COOKIE_NAME = "CurrentEmployee";

    public static UUID getCurrentUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        UUID result = UUIDUtil.EMPTY;
        String uid = CollectionUtil.firstOrDefault(httpServletRequest.getParameterValues(COOKIE_NAME));
        if (!StringUtil.isNullOrEmpty(uid)) {
            result = UUID.fromString(uid);
            setUserInCookies(result, httpServletResponse);
        } else {
            if (httpServletRequest.getCookies() != null) {
                for (Cookie cookie : httpServletRequest.getCookies()) {
                    if (cookie.getName().equals(COOKIE_NAME)) {
                        result = UUID.fromString(cookie.getValue());
                    }
                }
            }
        }
        if (result.equals(UUIDUtil.EMPTY)) {
            List<Employee> employeeList = EmployeeHelper.getAll();
            if (!employeeList.isEmpty()) {
                result = employeeList.get(0).getId();
                setUserInCookies(result, httpServletResponse);
            }
        }
        return result;
    }

    public static void setUserInCookies(UUID userId, HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(COOKIE_NAME, userId.toString());
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }
}