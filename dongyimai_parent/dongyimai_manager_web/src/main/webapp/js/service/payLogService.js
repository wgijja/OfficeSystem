app.service('payLogService', function ($http) {

    this.search = function (page, rows,searchEntity) {
        return $http.post('../payLog/search.do?page='+page+'&rows='+rows,searchEntity);
    }

});