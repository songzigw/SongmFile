<%@page language="java" contentType="application/json; charset=UTF-8"
   pageEncoding="UTF-8" session="false" isELIgnored="false"%>
<%@page import="cn.songm.common.utils.JsonUtils"%>
<%@page import="cn.songm.common.beans.Result"%>
<%@page import="cn.songm.file.service.FileError"%>

<%
Result<Object> result = new Result<Object>();
result.setSucceed(false);
result.setErrorCode(FileError.FIL_OUTSIZE.getErrCode());
result.setErrorDesc("上传的文件不能超过5M");
out.print(JsonUtils.getInstance().toJson(result));
%>