<?php

use \Phalcon\Mvc\Router;

$router = new Router(false);

$router->notFound([
    'namespace'     => 'GameAPIs\Controllers\Overview',
    'controller'    => 'index',
    'action'        => 'notfound',
]);

$router->add('/', [
    'namespace'     => 'GameAPIs\Controllers\Overview',
    'controller'    => 'index',
    'action'        => 'index',
]);

$router->add('/docs/minecraft/:action', [
    'namespace'     => 'GameAPIs\Controllers\Documentation\Minecraft',
    'controller'    => 'index',
    'action'        => 1
]);

$router->add('/docs/minecraft/query/:controller', [
    'namespace'     => 'GameAPIs\Controllers\Documentation\Minecraft\Query',
    'controller'    => 1,
    'action'        => 'index'
]);

$router->add('/docs/minecraft/images/:controller', [
    'namespace'     => 'GameAPIs\Controllers\Documentation\Minecraft\Images',
    'controller'    => 1,
    'action'        => 'index'
]);

$router->add('/docs/minecraft/ecommerce/:controller', [
    'namespace'     => 'GameAPIs\Controllers\Documentation\Minecraft\Ecommerce',
    'controller'    => 1,
    'action'        => 'index'
]);

$router->add('/docs/minecraft/extra/:controller', [
    'namespace'     => 'GameAPIs\Controllers\Documentation\Minecraft\Extra',
    'controller'    => 1,
    'action'        => 'index'
]);

$router->setUriSource(Router::URI_SOURCE_SERVER_REQUEST_URI);
return $router;
