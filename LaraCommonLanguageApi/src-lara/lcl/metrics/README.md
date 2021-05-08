# LARAM metrics
 
In order to ensure that the metrics selected were relevant we implemented metrics with a high number of references in the literature. The metrics implemented can be divided into three sets: object-oriented, complexity metrics, and size metrics.

## Object-Oriented Metrics

Chidamber and Kemerer's (CK) metrics were among the first set metrics to analyze these systems and are able to measure the cohesion and coupling of objects. Li and Henry's metrics continue the work Chidamber and Kemerer and proposed a set of metrics to analyze the maintainability of these systems.

### Chidamber and Kemerer Metrics
Chidamber and Kemerer Metrics are a set of 6 different measures designed to evaluate several aspects of OO systems. A total of six metrics were proposed:

* **Weighted Methods per Class (WMC)** sums all the complexities of the methods in the class. Since it is not specified how to calculate the complexity of a method, it was chosen to calculate the cyclomatic complexity.

* **Depth of Inheritance Tree (DIT)** measures the maximum inheritance path from the class to the root class.

* **Number of Children (NOC)** measures the number of immediate sub-classes of a class, we chose to only take into consideration explicit classes.

* **Lack of Cohesion of Methods (LCOM)**  describes the lack of cohesion among the methods of a class. This metric has many definitions and revisions and depending on the tool used, the results can be very different. The definition followed in LARAM is also referred to as LCOM94.

* **Response For a Class (RFC)** measures the number class methods plus all the methods called by the class. This metric is straightforward to calculate since there is an attribute to get all methods of a class, and retrieving the calls to others methods can be easily done using the Query API to get all \texttt{memberCalls}.

* **Coupling Between Object (CBO)** measures the number of classes to which a class is coupled. The types of coupling counted were: call to methods of other classes, all the classes extended, the types of fields, parameters and variable in methods, and the return types of each method. 

### Li and Henry Metrics
Li and Henry Metrics are a set of 5 metrics to evaluate the maintainability of OO systems. These metrics are very objective, so their implementation was more straightforward when compared with CK metrics. 

* **Number Of Methods (NOM)** measures the number of methods.

* **Size of procedures or functions (SIZEl)** counts the number of semicolons of each class. To calculate this metric, we obtained the source code of each class, and after removing all of the comments, we simply counted the number of remaining semicolons.

* **Size of procedures or functions (SIZE2)** measures the number of fields and methods of a class.

* **Message Passing Coupling (MPC)** counts the number of send statements, i.e., the number of function calls. So we searched the descendants of each function for calls to other methods and returned the number of distinct calls.

* **Data Abstraction Coupling (DAC)** counts the number of attributes that use another class as their type. For each field, it was taken into consideration not only the base type but also all inner types, e.g., a field of the type `Pair<String,Integer>` uses the types `Pair`, `String` and `Integer`.

## Complexity Metrics

The complexity metrics that we implemented were the cyclomatic and cognitive complexity. The former measures how difficult it is to test a code unit. The latter measures how difficult it is to read and understand the code. Both of these metrics can be calculated for the entire project, a file, a class, or a function.

* **Cognitive Complexity (CogniComplex)** measures the relative understandability of methods. There are three types of increments: general increments that simply increase the complexity by 1; nesting level increments that, as the name implies, increments the nesting level but does not increase the overall complexity; and the nesting increments which increase the overall complexity by the current nesting level. 

* **Cyclomatic complexity (CycloComplex)** measures the number of linearly independent paths through a program's source code.

## Size Metrics

The last set of metrics focuses on getting information about the high-level structure of the source code. As these are the most objective and straightforward metrics, they are also the easiest to implement and understand. 
The implemented metrics count:
* the number of classes **(NOCl)**;
* the number of files **(NOFi)**;
* the number of functions **(NOFu)**;
* the number of lines **(NOL)**.







