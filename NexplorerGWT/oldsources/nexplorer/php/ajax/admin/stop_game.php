<?php
	include "../../init.php";

	$gameSettings = Doctrine_Query::create()->from("Settings")->execute()->getFirst();
	$gameSettings->isRunning = 0;
	$gameSettings->save();
	
	$conn->commit();
?>