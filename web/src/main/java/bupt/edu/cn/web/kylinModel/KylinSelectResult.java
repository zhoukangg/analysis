package bupt.edu.cn.web.kylinModel;

import java.util.List;

/**
 * kylin查询返回结果对应的Java类
 */

public class KylinSelectResult {
    public boolean hitExceptionCache;
    public List<ColumnMeta> ColumnMetas;
    public double totalScanCount;
    public boolean isException;
    public double totalScanBytes;
    public boolean pushDown;
    public double duration;
    public boolean storageCacheUsed;
    public double affectedRowCount;
    public String cube;
    public List<List<String>> results;
    public String traceUrl;
    public boolean partial;
    public String exceptionMessage;

    public boolean isHitExceptionCache() {
        return hitExceptionCache;
    }

    public void setHitExceptionCache(boolean hitExceptionCache) {
        this.hitExceptionCache = hitExceptionCache;
    }

    public List<ColumnMeta> getColumnMetas() {
        return ColumnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> ColumnMetas) {
        this.ColumnMetas = ColumnMetas;
    }

    public double getTotalScanCount() {
        return totalScanCount;
    }

    public void setTotalScanCount(double totalScanCount) {
        this.totalScanCount = totalScanCount;
    }

    public boolean isException() {
        return isException;
    }

    public void setException(boolean exception) {
        isException = exception;
    }

    public double getTotalScanBytes() {
        return totalScanBytes;
    }

    public void setTotalScanBytes(double totalScanBytes) {
        this.totalScanBytes = totalScanBytes;
    }

    public boolean isPushDown() {
        return pushDown;
    }

    public void setPushDown(boolean pushDown) {
        this.pushDown = pushDown;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isStorageCacheUsed() {
        return storageCacheUsed;
    }

    public void setStorageCacheUsed(boolean storageCacheUsed) {
        this.storageCacheUsed = storageCacheUsed;
    }

    public double getAffectedRowCount() {
        return affectedRowCount;
    }

    public void setAffectedRowCount(double affectedRowCount) {
        this.affectedRowCount = affectedRowCount;
    }

    public String getCube() {
        return cube;
    }

    public void setCube(String cube) {
        this.cube = cube;
    }

    public List<List<String>> getResults() {
        return results;
    }

    public void setResults(List<List<String>> results) {
        this.results = results;
    }

    public String getTraceUrl() {
        return traceUrl;
    }

    public void setTraceUrl(String traceUrl) {
        this.traceUrl = traceUrl;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
