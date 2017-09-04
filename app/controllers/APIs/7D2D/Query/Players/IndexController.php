<?php

namespace GameAPIs\Controllers\APIs\SD2D\Query\Players;

use Redis;
use Phalcon\Filter;

class IndexController extends ControllerBase {

    public function indexAction() {
        $params = $this->dispatcher->getParams();
        if(empty($params['ip'])) {
            $output['error'] = "Please provide an address";
            $output['code'] = 001;
            echo json_encode($output, JSON_PRETTY_PRINT);
        } else {
            if(strpos($params['ip'], ',')) {
                if(count(explode(',', $params['ip'])) > 5) {
                    $output['error'] = "Address count > 5.";
                    $output['code'] = 002;
                    echo json_encode($output, JSON_PRETTY_PRINT);
                } else {
                    $this->dispatcher->forward(
                        [
                            "namespace"     => "GameAPIs\Controllers\APIs\SD2D\Query\Players",
                            "controller"    => "index",
                            "action"        => "multi"
                        ]
                    );
                }
            } else {
                $this->dispatcher->forward(
                    [
                        "namespace"     => "GameAPIs\Controllers\APIs\SD2D\Query\Players",
                        "controller"    => "index",
                        "action"        => "single"
                    ]
                );
            }
        }
    }

    public function singleAction() {
        require_once(APP_PATH . '/library/Multiple/Query/Dev/Autoloader.php');
        $params = $this->dispatcher->getParams();
        $filter = new Filter();
        if(strpos($params['ip'], ':')) {
            $explodeParams = explode(':', $params['ip']);
            $params['ip']   = $explodeParams[0];
            $params['port'] = $explodeParams[1] ?? 26900;
        } else {
            $params['port'] = 26900;
        }
        $cConfig = array();
        $cConfig['ip']   = $filter->sanitize($params['ip'], 'string');
        $cConfig['port'] = $params['port'];

        $cConfig['redis']['host'] = $this->config->application->redis->host;
        $cConfig['redis']['key']  = $this->config->application->redis->keyStructure->sd2d->ping.$cConfig['ip'].':'.$cConfig['port'];

        $redis = new Redis();
        $redis->pconnect($cConfig['redis']['host']);
        if($redis->exists($cConfig['redis']['key'])) {
            $response = json_decode(base64_decode($redis->get($cConfig['redis']['key'])),true);
            if(!$response['gq_online']) {
                $output['status']    = $response['gq_online'];
                $output['hostname']  = $response['gq_address'];
                $output['port']      = $response['gq_port_client'];
                $output['queryPort'] = $response['gq_port_query'];
                $output['protocol']  = $response['gq_transport'];
                $output['error']     = "Couldn't connect to address.";
                $output['code']      = 003;
            } else {
                if(in_array($response['game_dir'], array('7DTD','7D2D'))) {
                    $output['status']               = $response['gq_online'];
                    $output['hostname']             = $response['gq_address'];
                    $output['port']                 = $response['gq_port_client'];
                    $output['players']['online']    = $response['gq_numplayers'];
                    $output['players']['max']       = $response['gq_maxplayers'];
                    $output['protocol']             = $response['gq_transport'];
                } else {
                    $output['status']    = $response['gq_online'];
                    $output['hostname']  = $response['gq_address'];
                    $output['port']      = $response['gq_port_client'];
                    $output['queryPort'] = $response['gq_port_query'];
                    $output['protocol']  = $response['gq_transport'];
                    $output['error']     = "Server is not running 7 Days To Die. (".$response['game_descr']." - ".$response['game_id'].")";
                    $output['code']      = 004;
                }
            }
            $output['cached'] = true;
        } else {
            $GameQ = new \GameQ\GameQ();
            // Switch to multiple servers, sometimes people will either provide the correct query port or the join port.
            $GameQ->addServers(
                [
                    [
                        'type'  => 'sevendaystodie',
                        'host'  => $cConfig['ip'].':'.$cConfig['port'],
                        'id'    => 0
                    ],
                    [
                        'type' => 'sevendaystodie',
                        'host'=> $cConfig['ip'].':'.$cConfig['port'],
                        'id'    => 1,
                        'options' => [
                            'query_port' => $cConfig['port']
                        ]
                    ]
                ]
            );
            $GameQ->setOption('timeout', 3); // Russian servers have shitty filters causing their query time to sometimes be above 2 seconds.

            $responses = $GameQ->process();
            foreach ($responses as $resp) {
                if($resp['gq_online']) {
                    $response = $resp;
                }
            }
            if(empty($response)) {
                $response = $responses[0];
            }

            if(!$response['gq_online']) {
                $output['status']    = $response['gq_online'];
                $output['hostname']  = $response['gq_address'];
                $output['port']      = $response['gq_port_client'];
                $output['queryPort'] = $response['gq_port_query'];
                $output['protocol']  = $response['gq_transport'];
                $output['error']     = "Couldn't connect to address.";
                $output['code']      = 003;
            } else {
                if(in_array($response['game_dir'], array('7DTD','7D2D'))) {
                    $output['status']               = $response['gq_online'];
                    $output['hostname']             = $response['gq_address'];
                    $output['port']                 = $response['gq_port_client'];
                    $output['players']['online']    = $response['gq_numplayers'];
                    $output['players']['max']       = $response['gq_maxplayers'];
                    $output['protocol']             = $response['gq_transport'];
                } else {
                    $output['status']    = $response['gq_online'];
                    $output['hostname']  = $response['gq_address'];
                    $output['port']      = $response['gq_port_client'];
                    $output['queryPort'] = $response['gq_port_query'];
                    $output['protocol']  = $response['gq_transport'];
                    $output['error']     = "Server is not running 7 Days To Die. (".$response['game_descr']." - ".$response['game_id'].")";
                    $output['code']      = 004;
                }
            }
            $output['cached'] = false;
            $redis->set($cConfig['redis']['key'], base64_encode(json_encode($response, JSON_UNESCAPED_SLASHES|JSON_UNESCAPED_UNICODE)), 15);
        }
        echo json_encode($output, JSON_PRETTY_PRINT|JSON_UNESCAPED_SLASHES|JSON_UNESCAPED_UNICODE);
    }

    public function multiAction() {
        require_once(APP_PATH . '/library/Multiple/Query/Dev/Autoloader.php');
        $params = $this->dispatcher->getParams();
        $explodeComma = explode(',', $params['ip']);
        unset($params['ip']);
        $i=0;
        $cConfig = array();

        $cConfig['redis']['host'] = $this->config->application->redis->host;
        $redis = new Redis();
        $redis->pconnect($cConfig['redis']['host']);
        foreach ($explodeComma as $key => $value) {
            if(strpos($value, ':')) {
                $explodeParams = explode(':', $value);
                $cConfig['addresses'][$i]['ip'] = $explodeParams[0];
                $cConfig['addresses'][$i]['port'] = (int) $explodeParams[1];
            } else {
                $cConfig['addresses'][$i]['ip'] = $value;
                $cConfig['addresses'][$i]['port'] = 26900;
            }
            $i++;
        }
        foreach ($cConfig['addresses'] as $key => $value) {
            $combined = $value['ip'].':'.$value['port'];
            $combinedRedis = $this->config->application->redis->keyStructure->sd2d->ping.$combined;
            if($redis->exists($combinedRedis)) {
                $response = json_decode(base64_decode($redis->get($combinedRedis)),true);
                if(!$response['online']) {
                    $output[$combined]['status']    = $response['gq_online'];
                    $output[$combined]['hostname']  = $response['gq_address'];
                    $output[$combined]['port']      = $response['gq_port_client'];
                    $output[$combined]['queryPort'] = $response['gq_port_query'];
                    $output[$combined]['protocol']  = $response['gq_transport'];
                    $output[$combined]['error']     = "Couldn't connect to address.";
                    $output[$combined]['code']      = 003;
                } else {
                    if(in_array($response['game_dir'], array('7DTD','7D2D'))) {
                        $output[$combined]['status']            = $response['gq_online'];
                        $output[$combined]['hostname']          = $response['gq_address'];
                        $output[$combined]['port']              = $response['gq_port_client'];
                        $output[$combined]['queryPort']         = $response['gq_port_query'];
                        $output[$combined]['protocol']          = $response['gq_transport'];
                        $output[$combined]['players']['online'] = $response['gq_numplayers'];
                        $output[$combined]['players']['max']    = $response['gq_maxplayers'];
                    } else {
                        $output[$combined]['status']    = $response['gq_online'];
                        $output[$combined]['hostname']  = $response['gq_address'];
                        $output[$combined]['port']      = $response['gq_port_client'];
                        $output[$combined]['queryPort'] = $response['gq_port_query'];
                        $output[$combined]['protocol']  = $response['gq_transport'];
                        $output[$combined]['error']     = "Server is not running 7 Days To Die. (".$response['game_descr']." - ".$response['game_id'].")";
                        $output[$combined]['code']      = 004;
                    }
                }
                $output[$combined]['cached'] = true;
            } else {
                $GameQ = new \GameQ\GameQ();
                // Switch to multiple servers, sometimes people will either provide the correct query port or the join port.
                $GameQ->addServers(
                    [
                        [
                            'type'  => 'sevendaystodie',
                            'host'  => $combined,
                            'id'    => 0
                        ],
                        [
                            'type'  => 'sevendaystodie',
                            'host'  => $value['ip'].':'.$value['port'],
                            'id'    => 1,
                            'options' => [
                                'query_port' => $value['port']
                            ]
                        ]
                    ]
                );
                $GameQ->setOption('timeout', 3); // Russian servers have shitty filters causing their query time to sometimes be above 2 seconds.

                $responses = $GameQ->process();
                foreach ($responses as $resp) {
                    if($resp['gq_online']) {
                        $response = $resp;
                    }
                }
                if(empty($response)) {
                    $response = $responses[0];
                }

                if(!$response['online']) {
                    $output[$combined]['status']    = $response['gq_online'];
                    $output[$combined]['hostname']  = $response['gq_address'];
                    $output[$combined]['port']      = $response['gq_port_client'];
                    $output[$combined]['queryPort'] = $response['gq_port_query'];
                    $output[$combined]['protocol']  = $response['gq_transport'];
                    $output[$combined]['error']     = "Couldn't connect to address.";
                    $output[$combined]['code']      = 003;
                } else {
                	if(in_array($response['game_dir'], array('7DTD','7D2D'))) {
                        $output[$combined]['status']            = $response['gq_online'];
                        $output[$combined]['hostname']          = $response['gq_address'];
                        $output[$combined]['port']              = $response['gq_port_client'];
                        $output[$combined]['queryPort']         = $response['gq_port_query'];
                        $output[$combined]['protocol']          = $response['gq_transport'];
                        $output[$combined]['players']['online'] = $response['gq_numplayers'];
                        $output[$combined]['players']['max']    = $response['gq_maxplayers'];
                    } else {
                        $output[$combined]['status']    = $response['gq_online'];
                        $output[$combined]['hostname']  = $response['gq_address'];
                        $output[$combined]['port']      = $response['gq_port_client'];
                        $output[$combined]['queryPort'] = $response['gq_port_query'];
                        $output[$combined]['protocol']  = $response['gq_transport'];
                        $output[$combined]['error']     = "Server is not running 7 Days To Die. (".$response['game_descr']." - ".$response['game_id'].")";
                        $output[$combined]['code']      = 004;
                    }
                }
                $output[$combined]['cached'] = false;
                $redis->set($combinedRedis, base64_encode(json_encode($response, JSON_UNESCAPED_SLASHES|JSON_UNESCAPED_UNICODE)), 15);
            }
        }
        echo json_encode($output, JSON_PRETTY_PRINT|JSON_UNESCAPED_SLASHES|JSON_UNESCAPED_UNICODE);
    }
}
