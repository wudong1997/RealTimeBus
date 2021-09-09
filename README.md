# 实时校车软件系统设计文档

## 1  文档简介
### 1.1文档目的与范围
本软件设计文档编写的目的是说明程序模块的设计考虑，包括程序描述、输入输出、算法和流程逻辑等，为软件程序编写和系统维护提供基础。  
### 1.2读者对象
本文档的预期读者为系统设计人员、软件开发人员、软件测试人员和项目评审人员。
### 1.3参考文献
```
[1]王进.  实时公交查询系统的优化设计和实现[D]. 北京邮电大学, 2013.  
[2]杨荫.  提醒平台[D]. 华南理工大学, 2013.  
[3]李紫阳.  基于Android平台的视频客户端的设计与实现[D]. 西安电子科技大学, 2014.  
[4]庄丽.  企业订单管理系统的设计与实现[D]. 吉林大学, 2012.  
[5]马志祥.  基于J2EE的海量数据虚拟存储管理平台的设计与实现[D]. 电子科技大学, 2014.  
[6]唐芳.  实时公交信息查询系统无线移动客户端的设计与实现[D]. 北京邮电大学, 2012.  
[9]郝晓龙.  基于B/S架构的库存管理系统的分析与设计[D]. 北京邮电大学, 2013.  
[8]刘宁.  基于Android平台的LBS应用设计与实现[D]. 西安电子科技大学, 2014.  
[9]陈武.  基于XMPP的企业级即时通信系统的研究与实现[D]. 武汉理工大学, 2009.  
[10]王永青.  基于PHP的物联网用户登录与数据解析的设计与实现[D]. 武汉理工大学, 2012.  
[11]张琦.  基于PHP的实验室公共服务系统的设计与实现[D]. 西安电子科技大学, 2014.  
[12]陈盛.  基于VB的企业人事管理系统的设计与实现[D]. 电子科技大学, 2012.  
[13]任俊国.  多数据源可控查询技术的研究与应用[D]. 山东科技大学, 2012.  
[14]满勤.  基于J2EE的B2C电子商务系统的设计与实现[D]. 电子科技大学, 2013.  
[15]白云鹏.  实时公交换乘系统的研究与开发[D]. 电子科技大学, 2013.  
[16]郭宏昌.  基于Android手机的实时公交查询系统设计与实现[D]. 西安市交通运输管理处, 2012.  
[17]宫文浩.  基于Android平台的火车公交出行软件设计[D]. 广西师范大学, 2013.  
[18] Lei Lin.  An Android Smartphone Application for Collecting, Sharing and Predicting 2 Border Crossing Wait Tim [D]. the State University of New York, 2014.  
[19] Andres Monzon,Sara Hernandez,Rocio Cascajo. Quality of Bus Services Performance: Benefits of Real Time Passenger Information Systems[J]. Transport and Telecommunication,2013,14(2).  
[20]Md. Matiur Rahman,S.C. Wirasinghe,Lina Kattan. Users' views on current and future real‐time bus information systems[J]. J. Adv. Transp.,2013,47(3).  
[21] CHEN Peiji. Real-time bus passenger flow statistics scheme based on light-sensitive wireless sensor network[A]. 中国自动化学会控制理论专业委员会、中国系统工程学会.第35届中国控制会议论文集（F）[C].2016:5.  
[22] 黄洁. 智能公交系统乘客满意度和行为意愿分析[D].华中科技大学,2009.  
[23] Lin, L., Q. Wang and A. W. Sadek. Border Crossing Delay Prediction Using Transient Multi- 2 server Queueing Models. Transportation Research Part A: Policy and Practice, Vol. 64, pp. 65- 3 91, 2014.  
[24] Steinfeld, A., J. Zimmerman, A. Tomasic, D. Yoo, and R. D. Aziz. Mobile Transit  Information from Universal Design and Crowdsourcing. Transportation Research Record:  Journal of the Transportation Research Board, Vol. 2217, No. 1, pp. 95-102, 2011.  
```
 
## 2  软件简介
### 2.1软件设计背景与意义
众多高校都有多个校区，学生往返不同校区大多会选择搭乘校车。校车准确的抵达和出发时间是每位师生最为关注的信息，而运行时刻表是大多高校了解校车运营信息的主要途径。运行时刻表为每位师生提供了一份详细的计划运营时刻信息。受实际情况约束，计划运营时刻信息往往不够准确，会出现晚点现象。以中国矿业大学为例，学校在安排车辆时，往往会在同一时间段内投入若干辆车，每辆车从一个校区到另一个校区后紧接着会再返回原先那个校区，如此循环。当路况良好时，校车基本按照时刻表时间运营，但路况拥堵时，校车就很难遵循时刻表上的时间来运营。此外，出于运营成本考虑，中国矿大在闲时时段未设置明确的时刻表，即两校车发车间隔为半个小时左右，各站点抵达时间不定。实际运营过程中，校车通常会比预期时间晚20~40分钟，而如此长时间的莫名等待对于大部分人而言是难以忍受的，尤其是在北方寒冷的夜晚。为此，借助当前科技手段准确而方便地掌握当前校车的准确位置信息及抵达时间显得尤为重要。  
  
本作品将借助大数据手段及手机APP为广大师生的出行提供实时校车信息服务。在本APP中，每位用户既是信息的提供者也是信息的享用者，需要各位用户获取校车实时位置信息的同时贡献自己的位置信息，通过挖掘用户的实时位置信息获取当前的校车位置及预计抵达时间。通过本APP，用户可以实时查询校车的实时运营信息，包括校车的运营路线、发车时间、实时位置信息、抵达某一站点的时间预计。  
### 2.2软件需求分析
系统的需求主要有：1）校车时刻表及路线查询；2）校车实时位置信息查询；3）抵达时间预计；4）地图服务。具体地，用户在乘坐校车的时候使用手机定位系统，将自己的位置信息上传至云数据库；等车用户则从云数据库中获取这些共享的数据，通过聚类分析后得到当前正在运行的一辆或者多辆校车的实时位置；最后，根据预测分析模型来预测校车到达某一位置的时间。  
 
## 3  软件开发、测试与运行环境 
### 3.1 运行环境
|运行平台|Android智能手机|
|--|--|
|Android版本|	5.0以上|
## 3.2 开发环境
|操作系统|Windows10|
|--|--|
|开发语言|Java|
|JDK版本|Jdk 1.8|
|开发平台|Android studio2.3.3|
|服务器|AVOS Cloud|
## 3.3开发平台简介
Android Studio开发平台
### 3.3.1 简介
Android Studio 是一个Android集成开发工具，基于IntelliJ IDEA. 类似 Eclipse ADT，Android Studio 提供了集成的 Android 开发工具用于开发和调试。  
在IDEA的基础上，Android Studio 提供：   
1.基于Gradle的构建支持  
2.Android 专属的重构和快速修复  
3.提示工具以捕获性能、可用性、版本兼容性等问题  
4.支持ProGuard 和应用签名  
5.基于模板的向导来生成常用的 Android 应用设计和组件  
6.功能强大的布局编辑器，可以让你拖拉 UI 控件并进行效果预览  
### 3.3.2优势
Android studio相比较于其他开发平台的优点有：  
1、Google推出的  
2、速度更快  
3、UI更漂亮  
4、更加智能  
5、整合了Gradle构建工具  
6、强大的UI编辑器  
7、内置终端  
8、更完善的插件系统  
9、完美整合版本控制系统  
### 3.3.3 工程结构

