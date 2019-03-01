## Find you not used API calls  

Collect api calls in java application and save to elasticsearch
And then see from Kibana :)  

---

> Getting started  

- ## Build  

```
$ git clone 
$ mvn clean package
```  

- ## Apply agent  

- **Properties**  

  - Dcounter.elasticsearch.host : elastic search hosts with "," delimiter 
  - Dcounter.buffer.size : log buffer max size
  - Dcounter.buffer.seconds : seconds of sending log

- **Normal use**  

```
$ java -javaagent:/path/to/api-calls-counter.jar -Dcounter.elasticsearch.host=http://127.0.0.1 -Dcounter.buffer.size=100 -Dcounter.buffer.seconds=10 -jar yourapp.jar
```  

- **Tomcat (bin/setenv.sh)**   

```
export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/path/to/api-calls-counter.jar
export CATALINA_OPTS="$CATALINA_OPTS -Dcounter.elasticsearch.host=http://127.0.0.1
export CATALINA_OPTS="$CATALINA_OPTS -Dcounter.buffer.size=100
export CATALINA_OPTS="$CATALINA_OPTS -Dcounter.buffer.seconds=10
```  

- **Tomcat (bin/setenv.bat)**  

```
set CATALINA_OPTS=%CATALINA_OPTS% -javaagent:/path/to/api-calls-counter.jar
set CATALINA_OPTS=%CATALINA_OPTS% -Dcounter.buffer.size=100
set CATALINA_OPTS=%CATALINA_OPTS% -Dcounter.buffer.seconds=10
```    

---  

> Elasticsearch log  

```aidl
http://192.168.79.130:9200/api-log*/_search
{
  ...
  "hits": {
    "total": 38,
    "max_score": 1,
    "hits": [
    {
      "_index": "api-log-20190302",
      "_type": "_doc",
      "_id": "fhhJOmkBxhGDCHxXkIOx",
      "_score": 1,
      "_source": {
        "method": "GET",
        "requestURI": "/first/name/hiva",
        "urlPattern": "/first/name/{pathVariable}",
        "requestAt": 1551460926549
      }
    }
    ...
}
```  



---  


> How changed your code  

- **Servlet source code**  

```aidl
public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
  // added code at runtime
  // create TraceContext instance at ThreadLocal
  // add Http request method, uri, time
  TraceMain.countApiCallsByServlet(req);
  
  try {
    HttpServletRequest request;
    HttpServletResponse response;
    try {
      request = (HttpServletRequest)req;
      response = (HttpServletResponse)res;
    } catch (ClassCastException var8) {
      throw new ServletException(lStrings.getString("http.non_http"));
    }

    this.service(request, response);
    TraceMain.disposeContext(); // added code
  } catch (Throwable var9) {
    TraceMain.disposeContext(); // added code
    throw var9;
  }
}
```  

- **Controller source code**  

```aidl
@RestController
@RequestMapping({"/first"})
public class FirstController {
    private static final Logger logger = LoggerFactory.getLogger(FirstController.class);

    public FirstController() {
    }

    @GetMapping({"/1"})
    public String method1() {
        // added code at runtime
        TraceMain.countApiCallsByControllerMethods("/first/1");
        logger.info("FirstController::method1()");
        return "method1";
    }
    
    @GetMapping({"/name/{pathVariable}"})
        public String method4(@PathVariable(name = "pathVariable") String value) {
            TraceMain.countApiCallsByControllerMethods("/first/name/{pathVariable}");
            logger.info("FirstController::method4() : {}", value);
            return "method4";
        }
    }
}
```  
  

## TODO  

- [x] building java agent project
- [x] trace http servlet
- [x] trace spring`s controllers
  - @Controller / @RequestController
    - @RequestMaping / @GetMapping @PostMapping @PutMapping @DeleteMapping @PatchMapping
- [x] collect api calls & report to elasticsearch
- [ ] trace jersey resources

## Reference  

- asm code  
; https://github.com/scouter-project/scouter/tree/master/scouter.agent.java  
- idea & log collector  
; http://woowabros.github.io/tools/2019/02/15/controller-log.html
