cmake_minimum_required(VERSION 3.10.2)

project("ctest")
#定义一个全局变量包含了所有要编译的C文件
file(GLOB BZIP bzip/*.c)
#导入头文件
include_directories(bzip)
add_library( # Sets the name of the library.
        native-lib
        SHARED
        native-lib.cpp
        bspatch.c
        bzip
        #将bzip下的.c文件添加到library
        BZIP)
find_library( # Sets the name of the path variable.
        log-lib
        log )
target_link_libraries( # Specifies the target library.
        native-lib
        ${log-lib} )
