REST-framework
==============

A framework in Java for developing RESTful Web Services (Very easy to use)<br>
An example about how to make a web by using this framework is in another repository(<a href="https://github.com/SalamaSoft/REST-HelloWorld">REST-HelloWorld</a>)<br>

------------------------------------------------------------------------------
* It is designed to be simple (looks simple and use simply)<br>
  For example, a service is just a common class:<br>
  <pre><code>
  public class TestService {
  
      public static String test1(String paramA, String paramB) {
          return "it's ok";
      }

     /**
      * The format of response can be xml or json.(xml when responseType is empty or "xml", json when responseType is "json")
      */
      @ReturnValueConverter(valueFromRequestParam = "responseType", 
			jsonpReturnVariableNameFromRequestParam="jsonpReturn",
			skipObjectConvert = false)
      public static TestData test2(String paramA, String paramB) {
          TestData data = xxxx;
          return data;
      }
  }
  </code></pre>
  
  The code in javascript is just like invoking a method:<br>
  <pre><code>
	$.ajax({
		url: "/testWS/cloudDataService.do",
		type: "post",
		dataType: "text",
		data: {
			serviceType: "com.salama.testws.service.TestService",
			serviceMethod: "test2",
			responseType: "xml",
			paramA: "p0",
			paramB: "p1"
		},
		success: function(data) {
			$('#result').text(data);
		},
		error: function(p0, p1, p2) {
			$('#result').text('Error');
		}
	});
    </code></pre>

------------------------------------------------------------------------------
* 1st version of this framework was made in March 2012, and it is stable currently.<br>
It has been used in more than 10 projects, so you can safely use it in your projects.<br>

* In fact it includes 2 style of web service. <br>
  --- One is called "SalamaInvokeService", its input and output are xml(defined format). It's the traditinal way(keep login status in session and cookie, and use filter to intercept unauthorized url)<br>
      It's been left behind, and I almost don't use it currently.<br>
  --- One is called "SalamaCloudDataService", it is standard RESTful web service. It's simpler and better support the ajax style front-end.<br>

* JSON source is copied to here for compiling, and maybe has been modified for some bugs(whatever, I can not remember whether it has been modified or not)<br>
