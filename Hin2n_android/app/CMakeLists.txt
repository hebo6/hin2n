project(Hin2n)
cmake_minimum_required(VERSION 3.4.1)

# N2n information
set(N2N_VERSION 2.9.0)
set(N2N_OSNAME ${CMAKE_SYSTEM})
set(N2N_MODIFY_VERSION v2s_0.1.0)
set(N2N_MODIFY_AUTHOR "switchwang(https://github.com/switch-st) zhangbz(https://github.com/zhangbz)")
add_definitions(-DCMAKE_BUILD)

INCLUDE(TestBigEndian)
TEST_BIG_ENDIAN(UIP_CONF_BYTE_ORDER)
if(${UIP_CONF_BYTE_ORDER} EQUAL 1)
set(UIP_CONF_BYTE_ORDER UIP_BIG_ENDIAN)
else(${UIP_CONF_BYTE_ORDER} EQUAL 1)
set(UIP_CONF_BYTE_ORDER UIP_LITTLE_ENDIAN)
endif(${UIP_CONF_BYTE_ORDER} EQUAL 1)

# OpenSSL
SET(OPENSSL_ROOT_DIR src/main/jniLibs/${ANDROID_ABI})
SET(OPENSSL_LIBRARIES_DIR "${OPENSSL_ROOT_DIR}")
SET(OPENSSL_INCLUDE_DIR ${OPENSSL_ROOT_DIR}/include)
SET(OPENSSL_LIBRARIES "crypto")
LINK_DIRECTORIES(${OPENSSL_LIBRARIES_DIR})
OPTION(N2N_OPTION_OPENSSL "Enable OpenSSL support" ON)
set(N2N_OPTION_OPENSSL ON)

if(CMAKE_BUILD_TYPE)
if(NOT ${CMAKE_BUILD_TYPE} STREQUAL Debug)
set(CMAKE_BUILD_TYPE Release)
endif(NOT ${CMAKE_BUILD_TYPE} STREQUAL Debug)
else(CMAKE_BUILD_TYPE)
set(CMAKE_BUILD_TYPE Release)
endif(CMAKE_BUILD_TYPE)
add_definitions(-DCMAKE_BUILD)

add_definitions(-DN2N_VERSION=\"${N2N_VERSION}\" -DN2N_OSNAME=\"${N2N_OSNAME}\" -DN2N_MODIFY_VERSION=\"${N2N_MODIFY_VERSION}\" -DN2N_MODIFY_AUTHOR=\"${N2N_MODIFY_AUTHOR}\" -DUIP_CONF_BYTE_ORDER=\"${UIP_CONF_BYTE_ORDER}\")
add_definitions(-DGIT_RELEASE="" -DPACKAGE_VERSION="${N2N_VERSION}" -DPACKAGE_OSNAME="${CMAKE_SYSTEM}")
include_directories(src/main/cpp src/main/cpp/edge_jni src/main/cpp/slog src/main/cpp/tun2tap src/main/cpp/uip)

add_library(edge_jni SHARED src/main/cpp/edge_jni/edge_jni.c)
target_link_libraries(edge_jni edge_v3)
target_link_libraries(edge_jni slog)
target_link_libraries(edge_jni ${OPENSSL_LIBRARIES})


# Configure n2n_v3 config.h
set(PACKAGE_NAME "n2n")
set(PACKAGE_VERSION "${N2N_VERSION}")
set(PACKAGE_STRING "${PACKAGE_NAME} ${PACKAGE_VERSION}")
set(PACKAGE_TARNAME "${PACKAGE_NAME}")
set(PACKAGE_BUGREPORT "https://github.com/ntop/n2n/issues")
set(PACKAGE_URL "https://github.com/ntop/n2n")
string(TIMESTAMP PACKAGE_BUILDDATE "%Y-%m-%d")

if(N2N_OPTION_ZSTD)
    set(HAVE_LIBZSTD 1)
endif()
if(N2N_OPTION_OPENSSL)
    set(HAVE_LIBCRYPTO 1)
endif()
if(N2N_OPTION_MINIUPNP)
    set(HAVE_LIBMINIUPNPC 1)
endif()
if(N2N_OPTION_NATPMP)
    set(HAVE_LIBNATPMP 1)
endif()
if(N2N_OPTION_CAP)
    set(HAVE_LIBCAP 1)
endif()
if(N2N_OPTION_PTHREAD)
    set(HAVE_LIBPTHREAD 1)
endif()

# OS specific defines for Android
set(HAVE_NETINET_IN_H 1)
set(HAVE_ARPA_INET_H 1)
set(HAVE_NETDB_H 1)
set(HAVE_SYS_SOCKET_H 1)
set(HAVE_SYS_TYPES_H 1)
set(HAVE_UNISTD_H 1)

configure_file(
    src/main/cpp/n2n_v3/cmake/config.h.in
    ${CMAKE_BINARY_DIR}/n2n_v3/include/config.h
)
include_directories(${CMAKE_BINARY_DIR}/n2n_v3/include)

add_library(edge_v3 SHARED
        src/main/cpp/n2n_v3/src/n2n.c
        src/main/cpp/n2n_v3/src/edge_management.c
        src/main/cpp/n2n_v3/src/management.c
        src/main/cpp/n2n_v3/src/json.c
        src/main/cpp/n2n_v3/src/curve25519.c
        src/main/cpp/n2n_v3/src/auth.c
        src/main/cpp/n2n_v3/src/edge_utils.c
        src/main/cpp/n2n_v3/src/wire.c
        src/main/cpp/n2n_v3/src/minilzo.c
        src/main/cpp/n2n_v3/src/speck.c
        src/main/cpp/n2n_v3/src/pearson.c
        src/main/cpp/n2n_v3/src/tf.c
        src/main/cpp/n2n_v3/src/header_encryption.c
        src/main/cpp/n2n_v3/src/transform_null.c
        src/main/cpp/n2n_v3/src/transform_tf.c
        src/main/cpp/n2n_v3/src/transform_aes.c
        src/main/cpp/n2n_v3/src/transform_speck.c
        src/main/cpp/n2n_v3/src/transform_cc20.c
        src/main/cpp/n2n_v3/src/transform_lzo.c
        src/main/cpp/n2n_v3/src/cc20.c
        src/main/cpp/n2n_v3/src/aes.c
        src/main/cpp/n2n_v3/src/network_traffic_filter.c
        src/main/cpp/n2n_v3/src/sn_selection.c
        src/main/cpp/n2n_v3/src/random_numbers.c
        src/main/cpp/edge_jni/tuntap_android_v2.c
        src/main/cpp/edge_jni/edge_android_v2.c
        )
target_compile_definitions(edge_v3 PUBLIC N2N_HAVE_AES HAVE_OPENSSL_1_1)
target_compile_options(edge_v3 PRIVATE -DN2N_V3)
target_compile_options(edge_v3 PRIVATE 
     $<$<COMPILE_LANGUAGE:C>:-Wno-implicit-function-declaration>
)
target_include_directories(edge_v3 PUBLIC
        src/main/cpp/n2n_v3/include
        ${OPENSSL_INCLUDE_DIR}
        )
target_link_libraries(edge_v3 uip)
target_link_libraries(edge_v3 ${OPENSSL_LIBRARIES})



add_library(uip SHARED
                src/main/cpp/uip/uip.c
                src/main/cpp/uip/uip_arp.c
                src/main/cpp/tun2tap/tun2tap.c
            )

add_library(slog SHARED src/main/cpp/slog/slog.c)
target_link_libraries(slog log)
