<?php

namespace GameAPIs\Controllers\Supported;

class IndexController extends ControllerBase {

    public function initialize() {
        $this->tag->setTitle("GameAPIs");
    }

    public function indexAction() {

    }

    public function minecraftAction() {
        $this->tag->prependTitle("Minecraft APIs - ");
    }

}
